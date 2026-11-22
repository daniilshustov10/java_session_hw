package com.urlshortener.cli;

import com.urlshortener.app.ApplicationRunner;
import com.urlshortener.app.UrlOpener;
import com.urlshortener.models.Link;
import com.urlshortener.models.User;
import com.urlshortener.services.link.LinkService;
import com.urlshortener.services.user.UserService;

import java.util.*;

public class ConsoleRunner implements ApplicationRunner {
    private User currentUser;
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final LinkService linkService;
    private final UrlOpener urlOpener;

    public ConsoleRunner(UserService userService, LinkService linkService, UrlOpener urlOpener) {
        this.userService = userService;
        this.linkService = linkService;
        this.urlOpener = urlOpener;
    }

    @Override
    public void start() {
        System.out.println("===================== ПРИЛОЖЕНИЕ ДЛЯ СОКРАЩЕНИЯ ССЫЛОК =======================");

        while (true) {
            final String user = currentUser == null
                    ? "не определён"
                    : currentUser.getId().toString();
            System.out.println();
            System.out.println("Текущий пользователь: " + user);

            showMenu(MenuConfig.MAIN_MENU);

            String command = scanner.nextLine().trim();

            switch (command) {
                case "1" -> createLink();
                case "2" -> navigateLink();
                case "3" -> showLinksList();
                case "4" -> deleteLink();
                case "5" -> showUserMenu();
                case "0" -> { return; }
                default -> System.out.println("Вы ввели неверное действие. Попробуйте еще раз");
            }
        }
    }

    private void createLink() {
        while (true) {
            System.out.println("=================== Создание ссылки ======================");
            System.out.println();
            System.out.println("Введите оригинальный URL или 0 для выхода в главное меню");
            System.out.print("> ");

            String originalUrl = scanner.nextLine().trim();

            if (originalUrl.equals("0")) {
                return;
            }

            final User user = currentUser == null
                    ? userService.create()
                    : currentUser;

            currentUser = user;

            try {
                final Link link = linkService.create(originalUrl, user.getId());

                System.out.println("Ссылка успешно создана: " + link.getShortUrl());

                return;
            } catch (Exception e) {
                System.out.println("При создании ссылки возникла ошибка: " + e.getMessage() + ". Попробуйте еще раз");
            }
        }
    }

    private void navigateLink() {
        if (currentUser == null) {
            System.out.println("Создайте пользователя для перехода по ссылке");
            return;
        }

        while (true) {
            System.out.println("=================== Переход по ссылке ======================");
            System.out.println();
            System.out.println("Введите короткую ссылку или 0 для выхода в главное меню");
            System.out.print("> ");

            String shortUrl = scanner.nextLine().trim();

            if (shortUrl.equals("0")) {
                return;
            }

            try {
                final String originalUrl = linkService.resolveShortUrl(shortUrl, currentUser.getId());

                urlOpener.open(originalUrl);

                linkService.registerNavigation(shortUrl, currentUser.getId());

                System.out.println("Переход по ссылке " + shortUrl + " успешно осуществлен");

                return;
            } catch (Exception e) {
                System.out.println("При переходе по ссылке возникла ошибка: " + e.getMessage() + ". Попробуйте еще раз");
            }
        }
    }

    private void showLinksList() {
        if (currentUser == null) {
            System.out.println("Создайте пользователя для просмотра ссылок");
            return;
        }

        System.out.println("=================== Мои ссылки ======================");
        System.out.println();

        try {
            final List<Link> links = linkService.getListByUser(currentUser.getId());

            links.forEach(link -> System.out.println(link.getShortUrl()));
            System.out.println();
        } catch (Exception e) {
            System.out.println("При получении списка ссылок возникла ошибка: " + e.getMessage());
        }

        while (true) {
            System.out.println("Введите 0 для выхода в главное меню");
            System.out.print("> ");

            String command = scanner.nextLine().trim();

            if (command.equals("0")) {
                return;
            }
        }
    }

    private void deleteLink() {
        if (currentUser == null) {
            System.out.println("Создайте пользователя для удаления ссылок");
            return;
        }

        while (true) {
            System.out.println("=================== Удаление ссылки ======================");
            System.out.println();
            System.out.println("Введите короткую ссылку для удаления или 0 для выхода в главное меню");
            System.out.print("> ");

            String shortUrl = scanner.nextLine().trim();

            if (shortUrl.equals("0")) {
                return;
            }

            try {
                linkService.delete(shortUrl, currentUser.getId());

                System.out.println("Ссылка успешно удалена");

                return;
            } catch (Exception e) {
                System.out.println("При удалении ссылки возникла ошибка: " + e.getMessage() + ". Попробуйте еще раз");
            }
        }
    }

    private void showUserMenu() {
        System.out.println("======================== Смена пользователя =========================");

        while (true) {
            showMenu(MenuConfig.USER_MENU);

            String command = scanner.nextLine().trim();

            switch (command) {
                case "1" -> { createNewUser(); return; }
                case "2" -> { changeUser(); return; }
                case "0" -> { return; }
                default -> System.out.println("Вы ввели неверное действие. Попробуйте еще раз");
            }
        }
    }

    private void createNewUser() {
        try {
            currentUser = userService.create();

            System.out.println("Пользователь " + currentUser.getId().toString() + " успешно создан");
        } catch (Exception e) {
            System.out.println("Не удалось создать пользователя. " + e.getMessage());
        }
    }

    private void changeUser() {
        while (true) {
            System.out.println("=================== Войти по UUID ======================");
            System.out.println();
            System.out.println("Введите UUID пользователя или 0 для выхода в главное меню");
            System.out.print("> ");

            String id = scanner.nextLine().trim();

            if (id.equals("0")) {
                return;
            }

            try {
                currentUser = userService.get(UUID.fromString(id))
                        .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

                System.out.println("Смена пользователя прошла успешно. Текущий пользователь - " + id);

                return;
            } catch (Exception e) {
                System.out.println("При смене пользователя возникла ошибка: " + e.getMessage() + ". Попробуйте еще раз");
            }
        }
    }

    private void showMenu(List<String> menuItems) {
        System.out.println();
        menuItems.forEach(System.out::println);
        System.out.println();
        System.out.print("Выберите действие: ");
    }
}

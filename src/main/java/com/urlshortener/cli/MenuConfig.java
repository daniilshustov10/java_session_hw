package com.urlshortener.cli;

import java.util.List;

public final class MenuConfig {
    private MenuConfig() {}

    public static final List<String> MAIN_MENU = List.of(
            "1. Создать ссылку",
            "2. Перейти по ссылке",
            "3. Посмотреть мои ссылки",
            "4. Удалить ссылку",
            "5. Сменить пользователя",
            "0. Выход"
    );

    public static final List<String> USER_MENU = List.of(
            "1. Создать нового пользователя",
            "2. Войти по UUID",
            "0. Выход"
    );
}

package com.urlshortener;

import com.urlshortener.app.ApplicationRunner;
import com.urlshortener.app.LinkCleanupScheduler;
import com.urlshortener.cli.DesktopUrlOpener;
import com.urlshortener.app.UrlOpener;
import com.urlshortener.cli.ConsoleRunner;
import com.urlshortener.models.Link;
import com.urlshortener.models.User;
import com.urlshortener.repositories.LinkRepository;
import com.urlshortener.repositories.Repository;
import com.urlshortener.repositories.UserRepository;
import com.urlshortener.services.link.LinkService;
import com.urlshortener.services.link.LinkServiceImpl;
import com.urlshortener.services.user.UserService;
import com.urlshortener.services.user.UserServiceImpl;

import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Repository<String, Link> linkRepository = new LinkRepository();
        LinkService linkService = new LinkServiceImpl(linkRepository);

        Repository<UUID, User> userRepository = new UserRepository();
        UserService userService = new UserServiceImpl(userRepository);

        UrlOpener urlOpener = new DesktopUrlOpener();

        ApplicationRunner applicationRunner = new ConsoleRunner(userService, linkService, urlOpener);

        try (LinkCleanupScheduler scheduler = new LinkCleanupScheduler(linkService)) {
            scheduler.start();
            applicationRunner.start();
        }
    }
}
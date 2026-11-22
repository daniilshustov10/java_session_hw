package com.urlshortener.app;

import com.urlshortener.services.link.LinkService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LinkCleanupScheduler implements AutoCloseable {
    private final LinkService linkService;
    private final ScheduledExecutorService scheduler;
    private final int intervalHours;

    public LinkCleanupScheduler(LinkService linkService) {
        this.linkService = linkService;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.intervalHours = Integer.parseInt(Environment.getVariable("CLEANUP_UNAVAILABLE_LINKS_INTERVAL_HOURS"));
    }

    public void start() {
        scheduler.scheduleAtFixedRate(
                this::cleanup,
                intervalHours,
                intervalHours,
                TimeUnit.HOURS
        );
    }

    private void cleanup() {
        try {
            linkService.deleteUnavailableLinks();
        } catch (Exception e) {
            System.out.println("Ошибка при очистке ссылок: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        scheduler.shutdown();
    }
}

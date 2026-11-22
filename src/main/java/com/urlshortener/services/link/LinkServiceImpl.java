package com.urlshortener.services.link;

import com.urlshortener.app.Environment;
import com.urlshortener.models.Link;
import com.urlshortener.repositories.Repository;
import org.apache.commons.validator.routines.UrlValidator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;

public class LinkServiceImpl implements LinkService {
    private final Repository<String, Link> linkRepository;

    public LinkServiceImpl(Repository<String, Link> linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    public List<Link> getListByUser(UUID userId) {
        return linkRepository.getList()
                .stream()
                .filter(link -> link.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public Link create(String url, UUID userId) {
        final UrlValidator urlValidator = UrlValidator.getInstance();

        if (!urlValidator.isValid(url)) {
            throw new IllegalArgumentException("Некорректный URL");
        }

        final String shortUrl = generateShortUrl(url, userId);
        final int clickLimit = Integer.parseInt(Environment.getVariable("LINK_NAVIGATION_LIMIT"));
        final Duration ttl = Duration.ofMinutes(Integer.parseInt(Environment.getVariable("LINK_LIFETIME_MINUTES")));
        final Link link = new Link(url, shortUrl, userId, clickLimit, ttl);

        return linkRepository.create(link);
    }

    @Override
    public String resolveShortUrl(String shortUrl, UUID userId) {
        final Link link = linkRepository.get(shortUrl)
                .orElseThrow(() -> new NoSuchElementException("Не найдена ссылка для перехода"));

        if (!link.getOwnerId().equals(userId)) {
            throw new SecurityException("Нельзя перейти по ссылке, принадлежащей другому пользователю");
        }

        if (link.isClickLimitReached()) {
            throw new IllegalStateException("Лимит переходов по ссылке исчерпан. Лимит - " + link.getClickLimit());
        }

        if (link.isExpired()) {
            throw new IllegalStateException("Время жизни ссылки истекло");
        }

        return link.getOriginalUrl();
    }

    @Override
    public void registerNavigation(String shortUrl, UUID userId) {
        final Link link = linkRepository.get(shortUrl)
                .orElseThrow(() -> new NoSuchElementException("Не найдена ссылка для регистрации перехода"));

        if (!link.getOwnerId().equals(userId)) {
            throw new SecurityException("Нельзя зарегистрировать переход по ссылке, принадлежащей другому пользователю");
        }

        link.incrementClickCount();

        linkRepository.update(link);
    }

    @Override
    public void delete(String shortUrl, UUID userId) {
        final Link link = linkRepository.get(shortUrl)
                .orElseThrow(() -> new NoSuchElementException("Не найдена ссылка для удаления"));

        if (!link.getOwnerId().equals(userId)) {
            throw new SecurityException("Нельзя удалять ссылку другого пользователя");
        }

        linkRepository.delete(shortUrl);
    }

    @Override
    public void deleteUnavailableLinks() {
        linkRepository.getList().forEach((Link link) -> {
            if (link.isUnavailable()) {
                linkRepository.delete(link.getShortUrl());
            }
        });
    }

    private String generateShortUrl(String originalUrl, UUID userId) {
        final String input = userId + ":" + originalUrl;

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");

            final byte[] hash = digest.digest(
                    input.getBytes(StandardCharsets.UTF_8)
            );

            return "clck.ru/" + Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash)
                    .substring(0, 8);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Не удалось сгенерировать короткую ссылку", e);
        }
    }
}

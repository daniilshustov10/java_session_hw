package com.urlshortener.repositories;

import com.urlshortener.models.Link;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LinkRepository implements Repository<String, Link> {
    private final Map<String, Link> links = new ConcurrentHashMap<>();

    @Override
    public Link create(Link link) {
        Objects.requireNonNull(link, "Ссылка не может быть null");

        final Link existingLink = links.putIfAbsent(link.getShortUrl(), link);

        if (existingLink != null) {
            throw new IllegalArgumentException("Ссылка уже существует");
        }

        return link;
    }

    @Override
    public Link update(Link link) {
        Objects.requireNonNull(link, "Ссылка не может быть null");

        final Link previousLink = links.replace(link.getShortUrl(), link);

        if (previousLink == null) {
            throw new NoSuchElementException("Ссылки для обновления не существует");
        }

        return link;
    }

    @Override
    public List<Link> getList() {
        return List.copyOf(links.values());
    }

    @Override
    public Optional<Link> get(String shortUrl) {
        return Optional.ofNullable(links.get(shortUrl));
    }

    @Override
    public void delete(String shortUrl) {
        final Link removedLink = links.remove(shortUrl);

        if (removedLink == null) {
            throw new NoSuchElementException("Ссылки для удаления не существует");
        }
    }
}

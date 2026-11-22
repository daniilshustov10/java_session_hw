package com.urlshortener.repositories;

import com.urlshortener.models.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LinkRepositoryTest {
    private LinkRepository repository;
    private Link link;

    @BeforeEach
    void setUp() {
        repository = new LinkRepository();

        link = new Link(
                "https://example.com",
                "short.local/abc123",
                UUID.randomUUID(),
                5,
                Duration.ofMinutes(15)
        );
    }

    @Test
    void shouldCreateLink() {
        Link createdLink = repository.create(link);

        assertSame(link, createdLink);
        assertEquals(link, repository.get(link.getShortUrl()).orElseThrow());
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateLink() {
        repository.create(link);

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.create(link)
        );
    }

    @Test
    void shouldReturnLinkByShortUrl() {
        repository.create(link);

        Link foundLink = repository.get(link.getShortUrl()).orElseThrow();

        assertSame(link, foundLink);
    }

    @Test
    void shouldReturnAllLinks() {
        repository.create(link);

        List<Link> links = repository.getList();

        assertEquals(1, links.size());
        assertTrue(links.contains(link));
    }

    @Test
    void shouldDeleteExistingLink() {
        repository.create(link);

        repository.delete(link.getShortUrl());

        assertTrue(repository.get(link.getShortUrl()).isEmpty());
    }
}

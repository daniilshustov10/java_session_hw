package com.urlshortener.services.link;

import com.urlshortener.models.Link;
import com.urlshortener.repositories.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceImplTest {

    @Mock
    private Repository<String, Link> linkRepository;

    private LinkServiceImpl linkService;
    private UUID userId;
    private Link link;

    @BeforeEach
    void setUp() {
        linkService = new LinkServiceImpl(linkRepository);
        userId = UUID.randomUUID();

        link = new Link(
                "https://example.com",
                "short.local/abc123",
                userId,
                5,
                Duration.ofMinutes(15)
        );
    }

    @Test
    void shouldReturnLinksBelongingToUser() {
        UUID anotherUserId = UUID.randomUUID();

        Link anotherLink = new Link(
                "https://github.com",
                "short.local/xyz789",
                anotherUserId,
                5,
                Duration.ofMinutes(15)
        );

        when(linkRepository.getList())
                .thenReturn(List.of(link, anotherLink));

        List<Link> result = linkService.getListByUser(userId);

        assertEquals(1, result.size());
        assertSame(link, result.getFirst());
    }

    @Test
    void shouldRejectInvalidUrl() {
        assertThrows(
                IllegalArgumentException.class,
                () -> linkService.create("invalid-url", userId)
        );

        verifyNoInteractions(linkRepository);
    }

    @Test
    void shouldResolveExistingShortUrl() {
        when(linkRepository.get(link.getShortUrl()))
                .thenReturn(Optional.of(link));

        String result = linkService.resolveShortUrl(
                link.getShortUrl(),
                userId
        );

        assertEquals(link.getOriginalUrl(), result);
    }

    @Test
    void shouldThrowExceptionWhenShortUrlNotFound() {
        when(linkRepository.get("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> linkService.resolveShortUrl("unknown", userId)
        );
    }

    @Test
    void shouldRejectAccessToAnotherUsersLink() {
        when(linkRepository.get(link.getShortUrl()))
                .thenReturn(Optional.of(link));

        assertThrows(
                SecurityException.class,
                () -> linkService.resolveShortUrl(
                        link.getShortUrl(),
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void shouldRegisterNavigation() {
        when(linkRepository.get(link.getShortUrl()))
                .thenReturn(Optional.of(link));

        int initialClickCount = link.getClickCount();

        linkService.registerNavigation(link.getShortUrl(), userId);

        assertEquals(initialClickCount + 1, link.getClickCount());
        verify(linkRepository).update(link);
    }

    @Test
    void shouldDeleteOwnLink() {
        when(linkRepository.get(link.getShortUrl()))
                .thenReturn(Optional.of(link));

        linkService.delete(link.getShortUrl(), userId);

        verify(linkRepository).delete(link.getShortUrl());
    }

    @Test
    void shouldRejectDeletingAnotherUsersLink() {
        when(linkRepository.get(link.getShortUrl()))
                .thenReturn(Optional.of(link));

        assertThrows(
                SecurityException.class,
                () -> linkService.delete(
                        link.getShortUrl(),
                        UUID.randomUUID()
                )
        );

        verify(linkRepository, never()).delete(anyString());
    }
}

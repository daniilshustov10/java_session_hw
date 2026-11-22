package com.urlshortener.cli;

import com.urlshortener.app.UrlOpener;
import com.urlshortener.models.Link;
import com.urlshortener.models.User;
import com.urlshortener.services.link.LinkService;
import com.urlshortener.services.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsoleRunnerTest {

    @Mock
    private UserService userService;

    @Mock
    private LinkService linkService;

    @Mock
    private UrlOpener urlOpener;

    private InputStream originalSystemIn;
    private User user;

    @BeforeEach
    void setUp() {
        originalSystemIn = System.in;
        user = new User();
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalSystemIn);
    }

    private void runWithInput(String input) {
        System.setIn(new ByteArrayInputStream(
                input.getBytes(StandardCharsets.UTF_8)
        ));

        ConsoleRunner runner = new ConsoleRunner(
                userService,
                linkService,
                urlOpener
        );

        runner.start();
    }

    @Test
    void shouldExitApplication() {
        assertDoesNotThrow(() -> runWithInput("0\n"));

        verifyNoInteractions(userService, linkService, urlOpener);
    }

    @Test
    void shouldCreateNewUserFromUserMenu() {
        when(userService.create()).thenReturn(user);

        runWithInput("5\n1\n0\n");

        verify(userService).create();
    }

    @Test
    void shouldChangeCurrentUserByUuid() {
        UUID userId = user.getId();

        when(userService.get(userId))
                .thenReturn(Optional.of(user));

        runWithInput("5\n2\n" + userId + "\n0\n");

        verify(userService).get(userId);
    }

    @Test
    void shouldCreateLink() {
        String originalUrl = "https://example.com";

        Link link = new Link(
                originalUrl,
                "short.local/abc123",
                user.getId(),
                5,
                Duration.ofMinutes(15)
        );

        when(userService.create()).thenReturn(user);
        when(linkService.create(originalUrl, user.getId()))
                .thenReturn(link);

        runWithInput("1\n" + originalUrl + "\n0\n");

        verify(userService).create();
        verify(linkService).create(originalUrl, user.getId());
    }

    @Test
    void shouldNavigateLink() {
        String shortUrl = "short.local/abc123";
        String originalUrl = "https://example.com";

        when(userService.create()).thenReturn(user);
        when(linkService.resolveShortUrl(shortUrl, user.getId()))
                .thenReturn(originalUrl);

        runWithInput(
                "5\n1\n" +
                        "2\n" + shortUrl + "\n" +
                        "0\n"
        );

        verify(linkService).resolveShortUrl(shortUrl, user.getId());
        verify(urlOpener).open(originalUrl);
        verify(linkService).registerNavigation(shortUrl, user.getId());
    }

    @Test
    void shouldShowUsersLinks() {
        Link link = new Link(
                "https://example.com",
                "short.local/abc123",
                user.getId(),
                5,
                Duration.ofMinutes(15)
        );

        when(userService.create()).thenReturn(user);
        when(linkService.getListByUser(user.getId()))
                .thenReturn(List.of(link));

        runWithInput(
                "5\n1\n" +
                        "3\n0\n" +
                        "0\n"
        );

        verify(linkService).getListByUser(user.getId());
    }

    @Test
    void shouldDeleteLink() {
        String shortUrl = "short.local/abc123";

        when(userService.create()).thenReturn(user);

        runWithInput(
                "5\n1\n" +
                        "4\n" + shortUrl + "\n" +
                        "0\n"
        );

        verify(linkService).delete(shortUrl, user.getId());
    }

    @Test
    void shouldNotNavigateWithoutCurrentUser() {
        runWithInput("2\n0\n");

        verifyNoInteractions(linkService, urlOpener);
    }
}

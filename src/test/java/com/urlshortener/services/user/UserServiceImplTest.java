package com.urlshortener.services.user;

import com.urlshortener.models.User;
import com.urlshortener.repositories.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private Repository<UUID, User> userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.create(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.create();

        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());

        verify(userRepository).create(createdUser);
    }

    @Test
    void shouldReturnExistingUser() {
        User user = new User();

        when(userRepository.get(user.getId()))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.get(user.getId());

        assertTrue(result.isPresent());
        assertSame(user, result.orElseThrow());

        verify(userRepository).get(user.getId());
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.get(userId))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.get(userId);

        assertTrue(result.isEmpty());

        verify(userRepository).get(userId);
    }
}

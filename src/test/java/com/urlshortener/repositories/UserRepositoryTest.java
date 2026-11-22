package com.urlshortener.repositories;

import com.urlshortener.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    private UserRepository repository;
    private User user;

    @BeforeEach
    void setUp() {
        repository = new UserRepository();
        user = new User();
    }

    @Test
    void shouldCreateUser() {
        User createdUser = repository.create(user);

        assertSame(user, createdUser);
        assertSame(user, repository.get(user.getId()).orElseThrow());
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateUser() {
        repository.create(user);

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.create(user)
        );
    }

    @Test
    void shouldReturnUserById() {
        repository.create(user);

        User foundUser = repository.get(user.getId()).orElseThrow();

        assertSame(user, foundUser);
    }

    @Test
    void shouldReturnAllUsers() {
        repository.create(user);

        List<User> users = repository.getList();

        assertEquals(1, users.size());
        assertTrue(users.contains(user));
    }

    @Test
    void shouldDeleteExistingUser() {
        repository.create(user);

        repository.delete(user.getId());

        assertTrue(repository.get(user.getId()).isEmpty());
    }
}

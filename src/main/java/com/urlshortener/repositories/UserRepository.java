package com.urlshortener.repositories;

import com.urlshortener.models.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository implements Repository<UUID, User> {
    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    @Override
    public User create(User user) {
        Objects.requireNonNull(user, "Пользователь не может быть null");

        final User existingUser = users.putIfAbsent(user.getId(), user);

        if (existingUser != null) {
            throw new IllegalArgumentException("Пользователь уже существует");
        }

        return user;
    }

    @Override
    public User update(User user) {
        Objects.requireNonNull(user, "Пользователь не может быть null");

        final User previousUser = users.replace(user.getId(), user);

        if (previousUser == null) {
            throw new NoSuchElementException("Пользователя для обновления не существует");
        }

        return user;
    }

    @Override
    public List<User> getList() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> get(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(UUID id) {
        final User removedUser = users.remove(id);

        if (removedUser == null) {
            throw new NoSuchElementException("Пользователь для удаления не существует");
        }
    }
}

package com.urlshortener.services.user;

import com.urlshortener.models.User;
import com.urlshortener.repositories.Repository;

import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private final Repository<UUID, User> userRepository;

    public UserServiceImpl(Repository<UUID, User> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create() {
        final User user = new User();

        return userRepository.create(user);
    }

    @Override
    public Optional<User> get(UUID id) {
        return userRepository.get(id);
    }
}

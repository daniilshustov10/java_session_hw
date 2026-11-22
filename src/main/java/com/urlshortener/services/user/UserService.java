package com.urlshortener.services.user;

import com.urlshortener.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User create();
    Optional<User> get(UUID id);
}

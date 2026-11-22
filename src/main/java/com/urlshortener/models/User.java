package com.urlshortener.models;

import java.util.UUID;

public class User {
    private final UUID id = UUID.randomUUID();

    public UUID getId() {
        return id;
    }
}

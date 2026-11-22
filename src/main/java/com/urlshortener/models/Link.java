package com.urlshortener.models;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Link {
    private final UUID id = UUID.randomUUID();
    private final String originalUrl;
    private final String shortUrl;
    private final UUID ownerId;
    private final int clickLimit;
    private int clickCount = 0;
    private final Instant createdAt = Instant.now();
    private final Duration ttl;
    private final Instant expiresAt;

    public Link(String originalUrl, String shortUrl, UUID ownerId, int clickLimit, Duration ttl) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.ownerId = ownerId;
        this.clickLimit = clickLimit;
        this.ttl = ttl;
        this.expiresAt = createdAt.plus(ttl);
    }

    public void incrementClickCount() {
        this.clickCount++;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public int getClickCount() {
        return clickCount;
    }

    public int getClickLimit() {
        return clickLimit;
    }

    public Duration getTtl() {
        return ttl;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        final Instant now = Instant.now();

        return !now.isBefore(expiresAt);
    }

    public boolean isClickLimitReached() {
        return clickCount >= clickLimit;
    }

    public boolean isUnavailable() {
        return isExpired() || isClickLimitReached();
    }
}

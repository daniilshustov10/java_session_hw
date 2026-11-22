package com.urlshortener.services.link;

import com.urlshortener.models.Link;

import java.util.List;
import java.util.UUID;

public interface LinkService {
    List<Link> getListByUser(UUID userId);
    Link create(String url, UUID userId);
    String resolveShortUrl(String shortUrl, UUID userId);
    void registerNavigation(String shortUrl, UUID userId);
    void delete(String shortUrl, UUID userId);
    void deleteUnavailableLinks();
}

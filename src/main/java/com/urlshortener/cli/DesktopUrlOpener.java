package com.urlshortener.cli;

import com.urlshortener.app.UrlOpener;

import java.awt.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;

public class DesktopUrlOpener implements UrlOpener {
    @Override
    public void open(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось открыть ссылку в браузере", e);
        }
    }
}

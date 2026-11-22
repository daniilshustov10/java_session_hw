package com.urlshortener.repositories;

import java.util.List;
import java.util.Optional;

public interface Repository<K, T> {
    List<T> getList();
    Optional<T> get(K key);
    T create(T entity);
    T update(T entity);
    void delete(K key);
}

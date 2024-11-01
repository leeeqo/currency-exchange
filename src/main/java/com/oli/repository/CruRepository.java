package com.oli.repository;

import java.util.List;
import java.util.Optional;

public interface CruRepository<T> {

    List<T> findAll();

    Optional<T> findById(Long id);

    T save(T obj);
}

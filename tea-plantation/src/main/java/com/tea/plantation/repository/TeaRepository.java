package com.tea.plantation.repository;

import com.tea.plantation.domain.Tea;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeaRepository extends MongoRepository<Tea, String> {
    boolean existsByUuid(UUID uuid);
    Optional<Tea> findByUuid(UUID uuid);
    Optional<Tea> findByUpc(String upc);
}

package com.tea.plantation.repository;

import com.tea.plantation.domain.Tea;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeaRepository extends MongoRepository<Tea, String> { }

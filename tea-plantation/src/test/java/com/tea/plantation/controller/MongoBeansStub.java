package com.tea.plantation.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@TestConfiguration
public class MongoBeansStub {
    @Bean
    MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }
}

package com.tea.plantation.configuration;

import com.tea.common.client.PoolingApacheHttpClientRestTemplateCustomizer;
import com.tea.common.exception.CommonControllerExceptionHandler;
import com.tea.common.messaging.AmqpConfig;
import com.tea.common.messaging.AmqpOrderProcessingConfig;
import io.mongock.runner.springboot.EnableMongock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableMongock
@EnableScheduling
@Import(value = {
        AmqpConfig.class,
        AmqpOrderProcessingConfig.class,
        CommonControllerExceptionHandler.class,
        PoolingApacheHttpClientRestTemplateCustomizer.class
})
public class CommonConfig {
    /**
     * Transaction Manager.
     * Needed to allow execution of migrations in transaction scope.
     */
    @Bean
    public MongoTransactionManager transactionManager(MongoTemplate mongoTemplate) {
        return new MongoTransactionManager(mongoTemplate.getMongoDatabaseFactory());
    }
}

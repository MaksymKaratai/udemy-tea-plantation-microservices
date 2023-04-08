package com.tea.common.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AmqpCommonBeansConfig.class)
public class AmqpConfig {
    public static final String INVENTORY_QUEUE = "inventory-queue";
    public static final String PACKAGING_QUEUE = "packaging-queue";

    @Bean
    public Queue inventoryQueue() {
        return new Queue(INVENTORY_QUEUE, true);
    }

    @Bean
    public Queue packagingQueue() {
        return new Queue(PACKAGING_QUEUE, true);
    }
}

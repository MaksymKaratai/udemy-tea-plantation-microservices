package com.tea.order.services.components;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_FAILED_ALLOCATION_EXCHANGE;

@TestConfiguration
public class AdditionalBeans {

    public static final String ALLOCATION_ERROR_TEST_QUEUE = "alocation-error-test-queue";

    @Bean
    public Declarables testErrorQueue(@Qualifier(ORDER_FAILED_ALLOCATION_EXCHANGE) Exchange errorExchange) {
        var queue = new Queue(ALLOCATION_ERROR_TEST_QUEUE);
        var binding = BindingBuilder.bind(queue).to(errorExchange).with("").noargs();
        return new Declarables(queue, binding);
    }
}

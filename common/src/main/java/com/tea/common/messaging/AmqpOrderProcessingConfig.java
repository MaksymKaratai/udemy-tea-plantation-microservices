package com.tea.common.messaging;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpOrderProcessingConfig {
    public static final String ORDER_PROCESSING_EXCHANGE = "order-processing-exchange";
    public static final String ORDER_FAILED_ALLOCATION_EXCHANGE = "order-failed-allocation-exchange";

    public static final String ORDER_VALIDATION_QUEUE = "order-validation-queue";
    public static final String ORDER_VALIDATION_ROUTING_KEY = "order-validation-routing-key";
    public static final String ORDER_VALIDATION_RESULT_QUEUE = "order-validation-result-queue";
    public static final String ORDER_VALIDATION_RESULT_ROUTING_KEY = "order-validation-result-routing-key";

    public static final String ORDER_ALLOCATION_QUEUE = "order-allocation-queue";
    public static final String ORDER_ALLOCATION_ROUTING_KEY = "order-allocation-routing-key";
    public static final String ORDER_ALLOCATION_RESULT_QUEUE = "order-allocation-result-queue";
    public static final String ORDER_ALLOCATION_RESULT_ROUTING_KEY = "order-allocation-result-routing-key";

    public static final String ORDER_CANCEL_QUEUE = "order-cancel-queue";
    public static final String ORDER_CANCEL_ROUTING_KEY = "order-cancel-routing-key";

    @Bean(name = ORDER_PROCESSING_EXCHANGE)
    public Exchange orderingExchange() {
        return new DirectExchange(ORDER_PROCESSING_EXCHANGE, true, false);
    }

    @Bean(name = ORDER_FAILED_ALLOCATION_EXCHANGE)
    public Exchange failedAllocationExchange() {
        return new FanoutExchange(ORDER_FAILED_ALLOCATION_EXCHANGE, true, false);
    }

    @Bean
    @Autowired
    public Declarables validationAmqpDeclarables(@Qualifier(ORDER_PROCESSING_EXCHANGE) Exchange exchange) {
        return bindEventAndResultQueuesTo(exchange, ORDER_VALIDATION_QUEUE, ORDER_VALIDATION_ROUTING_KEY,
                ORDER_VALIDATION_RESULT_QUEUE, ORDER_VALIDATION_RESULT_ROUTING_KEY);
    }

    @Bean
    @Autowired
    public Declarables allocationAmqpDeclarables(@Qualifier(ORDER_PROCESSING_EXCHANGE) Exchange exchange) {
        return bindEventAndResultQueuesTo(exchange, ORDER_ALLOCATION_QUEUE, ORDER_ALLOCATION_ROUTING_KEY,
                ORDER_ALLOCATION_RESULT_QUEUE, ORDER_ALLOCATION_RESULT_ROUTING_KEY);
    }

    @Bean
    @Autowired
    public Declarables cancelQueueDeclarables(@Qualifier(ORDER_PROCESSING_EXCHANGE) Exchange exchange) {
        Queue queue = new Queue(ORDER_CANCEL_QUEUE, true);
        return new Declarables(queue, BindingBuilder.bind(queue).to(exchange).with(ORDER_CANCEL_ROUTING_KEY).noargs());
    }

    Declarables bindEventAndResultQueuesTo(Exchange targetExchange, String eventQName, String eventQKey, String resQName, String resQKey) {
        var eventQueue = new Queue(eventQName, true);
        var binding1 = BindingBuilder.bind(eventQueue).to(targetExchange).with(eventQKey).noargs();
        var resultQueue = new Queue(resQName, true);
        var binding2 = BindingBuilder.bind(resultQueue).to(targetExchange).with(resQKey).noargs();
        return new Declarables(eventQueue, binding1, resultQueue, binding2);
    }
}

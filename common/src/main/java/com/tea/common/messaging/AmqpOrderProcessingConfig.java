package com.tea.common.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpOrderProcessingConfig {
    public static final String ORDER_PROCESSING_EXCHANGE = "order-processing-exchange";

    public static final String ORDER_VALIDATION_QUEUE = "order-validation-queue";
    public static final String ORDER_VALIDATION_ROUTING_KEY = "order-validation-routing-key";
    public static final String ORDER_VALIDATION_RESULT_QUEUE = "order-validation-result-queue";
    public static final String ORDER_VALIDATION_RESULT_ROUTING_KEY = "order-validation-result-routing-key";

    public static final String ORDER_ALLOCATION_QUEUE = "order-allocation-queue";
    public static final String ORDER_ALLOCATION_ROUTING_KEY = "order-allocation-routing-key";
    public static final String ORDER_ALLOCATION_RESULT_QUEUE = "order-allocation-result-queue";
    public static final String ORDER_ALLOCATION_RESULT_ROUTING_KEY = "order-allocation-result-routing-key";

    @Bean(name = ORDER_PROCESSING_EXCHANGE)
    public Exchange orderingExchange() {
        return new DirectExchange(ORDER_PROCESSING_EXCHANGE, true, false);
    }

    @Bean(name = ORDER_VALIDATION_QUEUE)
    public Queue orderValidationQueue() {
        return new Queue(ORDER_VALIDATION_QUEUE, true);
    }

    @Bean
    @Autowired
    public Binding orderValidationBinding(@Qualifier(ORDER_PROCESSING_EXCHANGE) Exchange exchange,
                                          @Qualifier(ORDER_VALIDATION_QUEUE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_VALIDATION_ROUTING_KEY).noargs();
    }

    @Bean(name = ORDER_VALIDATION_RESULT_QUEUE)
    public Queue orderValidationResultQueue() {
        return new Queue(ORDER_VALIDATION_RESULT_QUEUE, true);
    }

    @Bean
    @Autowired
    public Binding orderValidationResultBinding(@Qualifier(ORDER_PROCESSING_EXCHANGE) Exchange exchange,
                                                @Qualifier(ORDER_VALIDATION_RESULT_QUEUE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_VALIDATION_RESULT_ROUTING_KEY).noargs();
    }

    @Bean(ORDER_ALLOCATION_QUEUE)
    public Queue orderAllocationQueue() {
        return new Queue(ORDER_ALLOCATION_QUEUE, true);
    }

    @Bean
    @Autowired
    public Binding orderAllocationBinding(@Qualifier(ORDER_PROCESSING_EXCHANGE) Exchange exchange,
                                          @Qualifier(ORDER_ALLOCATION_QUEUE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_ALLOCATION_ROUTING_KEY).noargs();
    }

    @Bean(ORDER_ALLOCATION_RESULT_QUEUE)
    public Queue orderAllocationResultQueue() {
        return new Queue(ORDER_ALLOCATION_RESULT_QUEUE, true);
    }

    @Bean
    @Autowired
    public Binding orderAllocationResultBinding(@Qualifier(ORDER_PROCESSING_EXCHANGE) Exchange exchange,
                                                @Qualifier(ORDER_ALLOCATION_RESULT_QUEUE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_ALLOCATION_RESULT_ROUTING_KEY).noargs();
    }
}

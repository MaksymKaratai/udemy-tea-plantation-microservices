package com.tea.order.services.amqp;

import com.tea.common.messaging.event.order.OrderValidatedEvent;
import com.tea.order.services.OrderCoordinatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_VALIDATION_RESULT_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderValidationListener {
    private final OrderCoordinatorService coordinatorService;

    @Transactional
    @RabbitListener(queues = ORDER_VALIDATION_RESULT_QUEUE)
    public void onOrderValidated(OrderValidatedEvent event) {
        UUID orderId = Objects.requireNonNull(event.orderId(), "Order id in validation result can't be null");
        log.debug("Got validation result for order[{}]; Order valid=[{}]", orderId, event.isValid());
        coordinatorService.handleValidationResult(orderId, event.isValid());
    }
}

package com.tea.plantation.amqp;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.dto.order.TeaOrderLineDto;
import com.tea.common.messaging.event.order.OrderValidatedEvent;
import com.tea.common.messaging.event.order.ValidateOrderEvent;
import com.tea.plantation.services.order.OrderLinesValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_PROCESSING_EXCHANGE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_VALIDATION_QUEUE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_VALIDATION_RESULT_ROUTING_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationEventListener {
    private final AmqpTemplate template;
    private final OrderLinesValidator validator;

    @Transactional
    @RabbitListener(queues = ORDER_VALIDATION_QUEUE)
    public void onValidationEvent(ValidateOrderEvent event) {
        TeaOrderDto order = Objects.requireNonNull(event.order(), "Order in validation event cant be null");
        log.debug("Got validation request for order[{}]", order.getId());
        List<TeaOrderLineDto> orderLines = order.getOrderLines();
        boolean linesValid = validator.areOrderLinesValid(orderLines);
        var validatedEvent = new OrderValidatedEvent(order.getId(), linesValid);
        template.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_VALIDATION_RESULT_ROUTING_KEY, validatedEvent);
        log.debug("Finished validation and send appropriate event");
    }
}

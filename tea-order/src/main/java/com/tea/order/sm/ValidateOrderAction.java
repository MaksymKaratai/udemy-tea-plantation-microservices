package com.tea.order.sm;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.messaging.event.order.ValidateOrderEvent;
import com.tea.order.domain.OrderStatus;
import com.tea.order.services.OrderCoordinatorService;
import com.tea.order.services.TeaOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderAction implements Action<OrderStatus, OrderEvent> {
    private final AmqpTemplate template;
    private final TeaOrderService service;

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> context) {
        Message<OrderEvent> message = context.getMessage();
        MessageHeaders headers = message.getHeaders();
        var id = (UUID) headers.get(OrderCoordinatorService.ORDER_ID_HEADER);
        TeaOrderDto order = service.findOrder(id);

        ValidateOrderEvent event = new ValidateOrderEvent(order);
        //todo add exchange, binding, and queue
        template.convertAndSend("order-processing-exchange", "validate-order-queue", event);
        log.debug("Sent validation event for order[{}]", id);
    }
}

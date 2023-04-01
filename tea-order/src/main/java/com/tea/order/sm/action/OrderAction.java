package com.tea.order.sm.action;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.order.domain.OrderStatus;
import com.tea.order.services.OrderCoordinatorService;
import com.tea.order.services.TeaOrderService;
import com.tea.order.sm.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class OrderAction implements Action<OrderStatus, OrderEvent> {
    protected final AmqpTemplate template;
    protected final TeaOrderService service;

    protected TeaOrderDto orderBasedOnHeaders(StateContext<OrderStatus, OrderEvent> context) {
        Message<OrderEvent> message = context.getMessage();
        MessageHeaders headers = message.getHeaders();
        var id = (UUID) headers.get(OrderCoordinatorService.ORDER_ID_HEADER);
        return service.findOrder(id);
    }
}

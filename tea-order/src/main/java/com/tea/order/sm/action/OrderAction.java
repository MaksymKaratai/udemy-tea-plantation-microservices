package com.tea.order.sm.action;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.order.domain.OrderStatus;
import com.tea.order.domain.TeaOrder;
import com.tea.order.mapper.TeaOrderMapper;
import com.tea.order.repository.TeaOrderRepository;
import com.tea.order.services.OrderCoordinatorService;
import com.tea.order.sm.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class OrderAction implements Action<OrderStatus, OrderEvent> {
    protected final AmqpTemplate template;
    protected final TeaOrderMapper mapper;
    protected final TeaOrderRepository repository;

    protected TeaOrderDto orderBasedOnHeaders(StateContext<OrderStatus, OrderEvent> context) {
        MessageHeaders headers = context.getMessageHeaders();
        Object headerValue = headers.get(OrderCoordinatorService.ORDER_ID_HEADER);
        if (!(headerValue instanceof UUID id)) {
            throw new IllegalArgumentException("StateMachine message doesnt contain orderId");
        }
        TeaOrder teaOrder = repository.orderEntityById(id);
        return mapper.toDto(teaOrder);
    }
}

package com.tea.order.sm.action;

import com.tea.order.domain.OrderStatus;
import com.tea.order.services.OrderCoordinatorService;
import com.tea.order.sm.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidationFailedCompensationAction implements Action<OrderStatus, OrderEvent> {
    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> context) {
        // Just log, nothing to compensate actually :)
        MessageHeaders messageHeaders = context.getMessageHeaders();
        Object orderId = messageHeaders.get(OrderCoordinatorService.ORDER_ID_HEADER);
        log.info("Order with id[{}] didn't pass validation and marked with failed status", orderId);
    }
}

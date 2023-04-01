package com.tea.order.sm.action;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.messaging.event.order.ValidateOrderEvent;
import com.tea.order.domain.OrderStatus;
import com.tea.order.services.TeaOrderService;
import com.tea.order.sm.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_PROCESSING_EXCHANGE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_VALIDATION_ROUTING_KEY;

@Slf4j
@Component
public class ValidateOrderAction extends OrderAction {
    public ValidateOrderAction(AmqpTemplate template, TeaOrderService service) {
        super(template, service);
    }

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> context) {
        TeaOrderDto order = orderBasedOnHeaders(context);
        ValidateOrderEvent event = new ValidateOrderEvent(order);
        template.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_VALIDATION_ROUTING_KEY, event);
        log.debug("Sent validation event for order[{}]", order.getId());
    }
}

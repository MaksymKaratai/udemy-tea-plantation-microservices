package com.tea.order.sm.action;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.messaging.event.order.AllocateOrderEvent;
import com.tea.order.domain.OrderStatus;
import com.tea.order.services.TeaOrderService;
import com.tea.order.sm.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_PROCESSING_EXCHANGE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_ALLOCATION_ROUTING_KEY;

@Slf4j
@Component
public class AllocateOrderAction extends OrderAction {
    public AllocateOrderAction(AmqpTemplate template, TeaOrderService service) {
        super(template, service);
    }

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> context) {
        TeaOrderDto teaOrderDto = orderBasedOnHeaders(context);
        var event = new AllocateOrderEvent(teaOrderDto);
        template.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_ALLOCATION_ROUTING_KEY, event);
        log.debug("Sent allocation event for order[{}]", teaOrderDto.getId());
    }
}

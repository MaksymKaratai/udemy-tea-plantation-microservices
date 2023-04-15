package com.tea.order.sm.action;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.messaging.event.order.CancelOrderEvent;
import com.tea.order.domain.OrderStatus;
import com.tea.order.mapper.TeaOrderMapper;
import com.tea.order.repository.TeaOrderRepository;
import com.tea.order.sm.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_CANCEL_ROUTING_KEY;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_PROCESSING_EXCHANGE;

@Slf4j
@Component
public class CancelAllocatedOrderAction extends OrderAction {
    public CancelAllocatedOrderAction(AmqpTemplate template, TeaOrderMapper mapper, TeaOrderRepository repository) {
        super(template, mapper, repository);
    }

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> context) {
        TeaOrderDto teaOrderDto = orderBasedOnHeaders(context);
        template.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_CANCEL_ROUTING_KEY, new CancelOrderEvent(teaOrderDto));
        log.debug("Sent cancel event for order[{}]", teaOrderDto.getId());
    }
}

package com.tea.order.sm.action;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.messaging.event.order.FailedOrderAllocationEvent;
import com.tea.order.domain.OrderStatus;
import com.tea.order.mapper.TeaOrderMapper;
import com.tea.order.repository.TeaOrderRepository;
import com.tea.order.sm.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_FAILED_ALLOCATION_EXCHANGE;

@Slf4j
@Component
public class AllocationFailedCompensationAction extends OrderAction {
    public AllocationFailedCompensationAction(AmqpTemplate template, TeaOrderMapper mapper, TeaOrderRepository repository) {
        super(template, mapper, repository);
    }

    @Override
    public void execute(StateContext<OrderStatus, OrderEvent> context) {
        // nothing to do on our side, notify all other interested services via exchange
        TeaOrderDto teaOrderDto = orderBasedOnHeaders(context);
        template.convertAndSend(ORDER_FAILED_ALLOCATION_EXCHANGE, StringUtils.EMPTY, new FailedOrderAllocationEvent(teaOrderDto));
        log.debug("Notified other services about order[{}] allocation failure", teaOrderDto.getId());
    }
}

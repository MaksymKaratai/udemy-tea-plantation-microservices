package com.tea.inventory.amqp;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.messaging.event.order.AllocateOrderEvent;
import com.tea.common.messaging.event.order.AllocationResultEvent;
import com.tea.inventory.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_ALLOCATION_QUEUE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_PROCESSING_EXCHANGE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_ALLOCATION_ROUTING_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationEventListener {
    private final AmqpTemplate template;
    private final AllocationService allocationService;

    @Transactional
    @RabbitListener(queues = ORDER_ALLOCATION_QUEUE)
    public void listen(AllocateOrderEvent event) {
        TeaOrderDto teaOrderDto = Objects.requireNonNull(event.orderDto(), "Order Dto can't be null");
        log.debug("Handle allocation event for order[{}]", teaOrderDto.getId());
        boolean hasErrors = false;
        boolean fullyAllocated = false;
        try {
            fullyAllocated = allocationService.allocateOrder(teaOrderDto);
        }
        catch (Exception e) {
            log.warn("Got exception[{}] during order[{}] processing", e.getMessage(), teaOrderDto.getId());
            hasErrors = true;
        }
        var resultEvent = new AllocationResultEvent(teaOrderDto, hasErrors, !fullyAllocated);
        template.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_ALLOCATION_ROUTING_KEY, resultEvent);
    }
}

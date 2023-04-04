package com.tea.order.amqp;

import com.tea.common.messaging.event.order.AllocationResultEvent;
import com.tea.order.services.OrderCoordinatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_ALLOCATION_RESULT_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationResultListener {
    private final OrderCoordinatorService coordinatorService;

    @Transactional
    @RabbitListener(queues = ORDER_ALLOCATION_RESULT_QUEUE)
    public void listen(AllocationResultEvent event) {
        var order = Objects.requireNonNull(event.orderDto(), "Order can't be null");
        log.debug("Got allocation result event for order[{}]", order.getId());
        if (event.hasErrors()) {
            coordinatorService.handleAllocationFailed(order.getId());
        }
        else if (event.pendingForInventory()) {
            coordinatorService.handlePendingForInventory(order);
        }
        else {
            coordinatorService.handleAllocationOk(order);
        }
    }
}

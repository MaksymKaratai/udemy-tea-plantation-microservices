package com.tea.order.sm;

import com.tea.order.domain.OrderStatus;
import com.tea.order.domain.TeaOrder;
import com.tea.order.domain.TeaOrderLine;
import com.tea.order.repository.TeaOrderRepository;
import com.tea.order.services.OrderCoordinatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderStatus, OrderEvent> {
    private final TeaOrderRepository repository;

    /**
     * Updates Order status and allocation details(if present) in the DB
     */
    @Override
    public void preStateChange(State<OrderStatus, OrderEvent> state, Message<OrderEvent> message,
                               Transition<OrderStatus, OrderEvent> transition,
                               StateMachine<OrderStatus, OrderEvent> stateMachine,
                               StateMachine<OrderStatus, OrderEvent> rootStateMachine) {
        if (message == null) {
            return;
        }
        MessageHeaders headers = message.getHeaders();
        var orderId = (UUID) headers.get(OrderCoordinatorService.ORDER_ID_HEADER);
        if (orderId == null) {
            return;
        }
        log.debug("Saving order[{}] state change[{}]", orderId, state.getId());
        TeaOrder reference = repository.getReferenceById(orderId);
        reference.setOrderStatus(state.getId());
        var allocationMap = (Map<?, ?>) headers.get(OrderCoordinatorService.ALLOCATION_MAP_HEADER);
        if (allocationMap != null) {
            populateAllocationDetails(reference, allocationMap);
        }
        repository.saveAndFlush(reference);
    }

    private void populateAllocationDetails(TeaOrder order, Map<?, ?> allocationMap) {
        log.debug("New allocations for order lines present, update them");
        List<TeaOrderLine> orderLines = order.getOrderLines();
        for (var line : orderLines) {
            Object amount = allocationMap.get(line.getId());
            if (amount instanceof Integer) {
                line.setQuantityAllocated((Integer) amount);
            }
        }
    }
}

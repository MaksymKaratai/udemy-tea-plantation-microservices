package com.tea.order.sm;

import com.tea.order.domain.OrderStatus;
import com.tea.order.domain.TeaOrder;
import com.tea.order.repository.TeaOrderRepository;
import com.tea.order.services.OrderCoordinatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderStatus, OrderEvent> {
    private final TeaOrderRepository repository;

    @Override
    public void preStateChange(State<OrderStatus, OrderEvent> state, Message<OrderEvent> message, Transition<OrderStatus, OrderEvent> transition, StateMachine<OrderStatus, OrderEvent> stateMachine, StateMachine<OrderStatus, OrderEvent> rootStateMachine) {
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable(message.getHeaders().get(OrderCoordinatorService.ORDER_ID_HEADER)))
                .ifPresent(orderId -> {
                    var id = (UUID) orderId;
                    log.debug("Saving order[{}] state change[{}]", id, state.getId());
                    TeaOrder reference = repository.getReferenceById(id);
                    reference.setOrderStatus(state.getId());
                    repository.saveAndFlush(reference);
                });
    }
}

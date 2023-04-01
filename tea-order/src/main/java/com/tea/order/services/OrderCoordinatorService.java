package com.tea.order.services;

import com.tea.order.domain.OrderStatus;
import com.tea.order.domain.TeaOrder;
import com.tea.order.repository.TeaOrderRepository;
import com.tea.order.sm.OrderEvent;
import com.tea.order.sm.OrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCoordinatorService {
    public static final String ORDER_ID_HEADER = "tea-order-id";

    private final TeaOrderRepository repository;
    private final OrderStateChangeInterceptor stateChangeInterceptor;
    private final StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;

    @Transactional
    public TeaOrder newOrder(TeaOrder teaOrder) {
        TeaOrder saved = repository.saveAndFlush(teaOrder);
        sendEvent(saved, OrderEvent.VALIDATE);
        return saved;
    }

    @Transactional
    public void handleValidationResult(UUID orderId, boolean isValid) {
        TeaOrder order = repository.getReferenceById(orderId);
        OrderEvent event = isValid ? OrderEvent.VALIDATION_OK : OrderEvent.VALIDATION_FAILED;
        sendEvent(order, event);
        if (isValid) {
            log.debug("Initiate allocation for order [{}]", orderId);
            TeaOrder lastOrder = repository.getReferenceById(orderId);
            sendEvent(lastOrder, OrderEvent.ALLOCATE);
        }
    }

    private void sendEvent(TeaOrder order, OrderEvent event) {
        StateMachine<OrderStatus, OrderEvent> stateMachine = getStateMachine(order);
        var message = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, order.getId())
                .build();
        stateMachine.sendEvent(Mono.just(message)).blockFirst();
    }

    private StateMachine<OrderStatus, OrderEvent> getStateMachine(TeaOrder order) {
        String machineId = order.getId().toString();
        var stateMachine = stateMachineFactory.getStateMachine(machineId);
        stateMachine.stopReactively().block();
        var context = new DefaultStateMachineContext<OrderStatus, OrderEvent>(
                order.getOrderStatus(), null, null, null, null);
        stateMachine.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(stateChangeInterceptor);
            sma.resetStateMachineReactively(context).block();
        });
        stateMachine.startReactively().block();
        return stateMachine;
    }
}

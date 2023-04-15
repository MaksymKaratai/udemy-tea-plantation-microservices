package com.tea.order.services;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.dto.order.TeaOrderLineDto;
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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCoordinatorService {
    public static final String ORDER_ID_HEADER = "tea-order-id";
    public static final String ALLOCATION_MAP_HEADER = "allocation-map";

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
            allocate(orderId);
        }
    }

    @Transactional
    public void allocate(UUID orderId) {
        log.debug("Initiate allocation for order [{}]", orderId);
        TeaOrder lastOrder = repository.getReferenceById(orderId);
        sendEvent(lastOrder, OrderEvent.ALLOCATE);
    }

    @Transactional// make 3 separate methods / add extra logic into interceptor to update allocated resources, use map in headers
    public void handleAllocationOk(TeaOrderDto orderDto) {
        sendEventWithAllocationDetails(orderDto, OrderEvent.ALLOCATION_OK);
    }

    @Transactional
    public void handleAllocationFailed(UUID orderId) {
        TeaOrder order = repository.getReferenceById(orderId);
        sendEvent(order, OrderEvent.ALLOCATION_FAILED);
    }

    @Transactional
    public void handlePendingForInventory(TeaOrderDto orderDto) {
        sendEventWithAllocationDetails(orderDto, OrderEvent.WAIT_FOR_INVENTORY);
    }

    @Transactional
    public void pickupOrder(UUID orderId) {
        TeaOrder teaOrder = repository.getReferenceById(orderId);
        sendEvent(teaOrder, OrderEvent.ORDER_PICK_UP);
    }

    private void sendEvent(TeaOrder order, OrderEvent event) {
        StateMachine<OrderStatus, OrderEvent> stateMachine = getStateMachine(order);
        var message = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, order.getId())
                .build();
        stateMachine.sendEvent(Mono.just(message)).blockFirst();
    }

    private void sendEventWithAllocationDetails(TeaOrderDto order, OrderEvent event) {
        List<TeaOrderLineDto> orderLines = order.getOrderLines();
        var allocationMap = new HashMap<>(orderLines.size());
        for (var line : orderLines) {
            allocationMap.put(line.getId(), line.getQuantityAllocated());
        }
        TeaOrder orderEntity = repository.getReferenceById(order.getId());
        var message = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, order.getId())
                .setHeader(ALLOCATION_MAP_HEADER, allocationMap)
                .build();
        StateMachine<OrderStatus, OrderEvent> stateMachine = getStateMachine(orderEntity);
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

package com.tea.order.services;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.dto.order.TeaOrderLineDto;
import com.tea.common.messaging.event.order.AllocateOrderEvent;
import com.tea.common.messaging.event.order.AllocationResultEvent;
import com.tea.common.messaging.event.order.CancelOrderEvent;
import com.tea.common.messaging.event.order.FailedOrderAllocationEvent;
import com.tea.common.messaging.event.order.OrderValidatedEvent;
import com.tea.common.messaging.event.order.ValidateOrderEvent;
import com.tea.order.domain.OrderStatus;
import com.tea.order.domain.TeaOrder;
import com.tea.order.domain.TeaOrderLine;
import com.tea.order.mapper.TeaOrderMapper;
import com.tea.order.repository.TeaOrderRepository;
import com.tea.order.services.components.AdditionalBeans;
import org.apache.commons.collections4.CollectionUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_ALLOCATION_QUEUE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_ALLOCATION_RESULT_ROUTING_KEY;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_CANCEL_QUEUE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_PROCESSING_EXCHANGE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_VALIDATION_QUEUE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_VALIDATION_RESULT_ROUTING_KEY;
import static com.tea.order.services.components.AdditionalBeans.ALLOCATION_ERROR_TEST_QUEUE;
import static java.util.concurrent.TimeUnit.SECONDS;


@Tag("IT")
@Testcontainers
@SpringBootTest
@Import(AdditionalBeans.class)
class OrderCoordinatorServiceIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("tea")
            .withUsername("order-user")
            .withPassword("123")
            .withUrlParam("currentSchema", "order-service");
    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.11-management-alpine");

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
    }

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    TeaOrderRepository repository;

    @Autowired
    TeaOrderMapper teaOrderMapper;

    @Autowired
    OrderCoordinatorService coordinatorService;

    @Test
    void coordinatorShouldMoveOrderFromNewTo_AllocatedStatus_WhenValidationAndAllocationWereSuccessful() {
        var order = TeaOrderDto.builder()
                .orderLines(List.of(orderLine(10), orderLine(5)))
                .build();
        TeaOrder entity = teaOrderMapper.toEntity(order);

        TeaOrder teaOrder = coordinatorService.newOrder(entity);

        Assertions.assertNotNull(teaOrder);
        Assertions.assertNotNull(teaOrder.getId());
        Assertions.assertEquals(OrderStatus.VALIDATING, teaOrder.getOrderStatus());

        // wait for validation event
        var validationReqType = new ParameterizedTypeReference<ValidateOrderEvent>(){};
        var validationEvent = amqpTemplate.receiveAndConvert(ORDER_VALIDATION_QUEUE, 1000, validationReqType);
        Assertions.assertNotNull(validationEvent);
        Assertions.assertNotNull(validationEvent.order());
        Assertions.assertEquals(teaOrder.getId(), validationEvent.order().getId());
        Assertions.assertTrue(CollectionUtils.isNotEmpty(validationEvent.order().getOrderLines()));

        // simulate successful response from plantation service via amqp
        var validatedEvent = new OrderValidatedEvent(validationEvent.order().getId(), true);
        amqpTemplate.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_VALIDATION_RESULT_ROUTING_KEY, validatedEvent);

        // check intermediate status
        Awaitility.await().atMost(2, SECONDS)
                .until(() -> OrderStatus.ALLOCATING.equals(repository.currentStatusById(teaOrder.getId())));

        // wait for allocation event
        var allocationReqType = new ParameterizedTypeReference<AllocateOrderEvent>(){};
        var allocationEvent = amqpTemplate.receiveAndConvert(ORDER_ALLOCATION_QUEUE, 1000, allocationReqType);
        Assertions.assertNotNull(allocationEvent);
        TeaOrderDto allocationOrder = allocationEvent.orderDto();
        Assertions.assertNotNull(allocationOrder);
        Assertions.assertEquals(teaOrder.getId(), allocationOrder.getId());
        Assertions.assertTrue(CollectionUtils.isNotEmpty(allocationOrder.getOrderLines()));
        // simulate successful response
        for (var line : allocationOrder.getOrderLines()) {
            line.setQuantityAllocated(line.getOrderQuantity());
        }
        var allocationResultEvent = new AllocationResultEvent(allocationOrder, false, false);
        amqpTemplate.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_ALLOCATION_RESULT_ROUTING_KEY, allocationResultEvent);

        // check intermediate status
        Awaitility.await().atMost(2, SECONDS)
                .until(() -> OrderStatus.ALLOCATED.equals(repository.currentStatusById(teaOrder.getId())));

        var resultOrderOptional = repository.findById(teaOrder.getId());
        Assertions.assertTrue(resultOrderOptional.isPresent());
        TeaOrder resultOrder = resultOrderOptional.get();
        Assertions.assertEquals(OrderStatus.ALLOCATED, resultOrder.getOrderStatus());
        for (var line : resultOrder.getOrderLines()) {
            Assertions.assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        }
    }

    @Test
    void coordinatorShouldMoveAllocatedOrderTo_PickedUpStatus() {
        var line = TeaOrderLine.builder()
                .orderQuantity(5)
                .quantityAllocated(5)
                .teaId(UUID.randomUUID())
                .build();
        var order = TeaOrder.builder()
                .orderLines(List.of(line))
                .orderStatus(OrderStatus.ALLOCATED)
                .build();

        TeaOrder allocated = repository.saveAndFlush(order);
        UUID orderId = allocated.getId();

        coordinatorService.pickupOrder(orderId);

        Awaitility.await().atMost(3, SECONDS)
                .until(() -> repository.currentStatusById(orderId).equals(OrderStatus.PICKED_UP));
    }

    @Test
    void coordinatorShouldMoveOrderTo_ValidationFailedStatus_WhenValidationFailed() {
        TeaOrder teaOrder = coordinatorService.newOrder(new TeaOrder());

        // wait for validation event
        var validationReqType = new ParameterizedTypeReference<ValidateOrderEvent>(){};
        var validationEvent = amqpTemplate.receiveAndConvert(ORDER_VALIDATION_QUEUE, 1000, validationReqType);
        Assertions.assertNotNull(validationEvent);
        Assertions.assertNotNull(validationEvent.order());

        // simulate fail response from plantation service via amqp
        var validatedEvent = new OrderValidatedEvent(validationEvent.order().getId(), false);
        amqpTemplate.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_VALIDATION_RESULT_ROUTING_KEY, validatedEvent);

        Awaitility.await().atMost(3, SECONDS)
                .until(() -> OrderStatus.VALIDATION_ERROR.equals(repository.currentStatusById(teaOrder.getId())));
    }

    @Test
    void coordinatorShouldMoveOrderTo_AllocationErrorStatus_WhenAllocationFailed() {
        TeaOrder teaOrder = coordinatorService.newOrder(new TeaOrder());

        // wait for validation event
        var validationReqType = new ParameterizedTypeReference<ValidateOrderEvent>(){};
        var validationEvent = amqpTemplate.receiveAndConvert(ORDER_VALIDATION_QUEUE, 1000, validationReqType);
        Assertions.assertNotNull(validationEvent);
        Assertions.assertNotNull(validationEvent.order());

        // simulate successful response from plantation service via amqp
        var validatedEvent = new OrderValidatedEvent(validationEvent.order().getId(), true);
        amqpTemplate.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_VALIDATION_RESULT_ROUTING_KEY, validatedEvent);

        // wait for allocation event
        var allocationReqType = new ParameterizedTypeReference<AllocateOrderEvent>(){};
        var allocationEvent = amqpTemplate.receiveAndConvert(ORDER_ALLOCATION_QUEUE, 1000, allocationReqType);
        Assertions.assertNotNull(allocationEvent);
        // simulate fail response
        var allocationResultEvent = new AllocationResultEvent(allocationEvent.orderDto(), true, false);
        amqpTemplate.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_ALLOCATION_RESULT_ROUTING_KEY, allocationResultEvent);

        Awaitility.await().atMost(3, SECONDS)
                .until(()-> OrderStatus.ALLOCATION_ERROR.equals(repository.currentStatusById(teaOrder.getId())));

        var orderFailedType = new ParameterizedTypeReference<FailedOrderAllocationEvent>(){};
        var failMsg = amqpTemplate.receiveAndConvert(ALLOCATION_ERROR_TEST_QUEUE, 2000, orderFailedType);
        Assertions.assertNotNull(failMsg);
        Assertions.assertNotNull(failMsg.failedOrder());
        Assertions.assertEquals(teaOrder.getId(), failMsg.failedOrder().getId());
    }

    @Test
    void coordinatorShouldMoveOrderTo_PendingInventoryStatusAndUpdateOrderLines_WhenAllocationResultHas_PendingForInventoryFlag() {
        TeaOrderDto order = TeaOrderDto.builder()
                .orderLines(List.of(orderLine(10), orderLine(16)))
                .build();
        TeaOrder teaOrder = coordinatorService.newOrder(teaOrderMapper.toEntity(order));

        // wait for validation event
        var validationReqType = new ParameterizedTypeReference<ValidateOrderEvent>(){};
        var validationEvent = amqpTemplate.receiveAndConvert(ORDER_VALIDATION_QUEUE, 1000, validationReqType);
        Assertions.assertNotNull(validationEvent);
        // simulate successful response from plantation service via amqp
        var validatedEvent = new OrderValidatedEvent(validationEvent.order().getId(), true);
        amqpTemplate.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_VALIDATION_RESULT_ROUTING_KEY, validatedEvent);

        // wait for allocation event
        var allocationReqType = new ParameterizedTypeReference<AllocateOrderEvent>(){};
        var allocationEvent = amqpTemplate.receiveAndConvert(ORDER_ALLOCATION_QUEUE, 1000, allocationReqType);
        Assertions.assertNotNull(allocationEvent);
        TeaOrderDto allocationDto = allocationEvent.orderDto();
        allocationDto.getOrderLines().forEach(line -> line.setQuantityAllocated(line.getOrderQuantity() / 2));
        amqpTemplate.convertAndSend(ORDER_PROCESSING_EXCHANGE, ORDER_ALLOCATION_RESULT_ROUTING_KEY,
                new AllocationResultEvent(allocationDto, false, true));

        Awaitility.await().atMost(3, SECONDS)
                .until(()-> OrderStatus.PENDING_INVENTORY.equals(repository.currentStatusById(teaOrder.getId())));
        TeaOrder lastOrder = repository.orderEntityById(teaOrder.getId());
        Assertions.assertTrue(CollectionUtils.isNotEmpty(lastOrder.getOrderLines()));
        for (var line : lastOrder.getOrderLines()) {
            Assertions.assertEquals(line.getOrderQuantity() / 2, line.getQuantityAllocated() );
        }
    }

    @Test
    void coordinatorShouldSendCancelEvent_WhenAllocatedOrderMovingToCancelStatus() {
        int quantity1 = 7;
        int quantity2 = 6;
        TeaOrderLineDto line = orderLine(quantity1);
        line.setQuantityAllocated(quantity1);
        TeaOrderLineDto line2 = orderLine(quantity2);
        line2.setQuantityAllocated(quantity2);
        TeaOrderDto orderDto = TeaOrderDto.builder()
                .id(UUID.randomUUID())
                .orderStatus(OrderStatus.ALLOCATED.name())
                .orderLines(List.of(line, line2))
                .build();
        TeaOrder order = repository.saveAndFlush(teaOrderMapper.toEntity(orderDto));

        coordinatorService.cancel(order.getId());

        Awaitility.await().atMost(3, SECONDS)
                .until(()-> OrderStatus.CANCELED.equals(repository.currentStatusById(order.getId())));

        var type = new ParameterizedTypeReference<CancelOrderEvent>(){};
        CancelOrderEvent cancelOrderEvent = amqpTemplate.receiveAndConvert(ORDER_CANCEL_QUEUE, 1000, type);
        Assertions.assertNotNull(cancelOrderEvent);
        Assertions.assertNotNull(cancelOrderEvent.orderDto());
        Assertions.assertEquals(order.getId(), cancelOrderEvent.orderDto().getId());
        Assertions.assertTrue(CollectionUtils.isNotEmpty(cancelOrderEvent.orderDto().getOrderLines()));
        for (var orderLine : cancelOrderEvent.orderDto().getOrderLines()) {
            Assertions.assertNotNull(orderLine.getOrderQuantity());
            Assertions.assertTrue(orderLine.getOrderQuantity() > 0);
            Assertions.assertEquals(orderLine.getOrderQuantity(), orderLine.getQuantityAllocated());
        }
    }

    private TeaOrderLineDto orderLine(int quantity) {
        return TeaOrderLineDto.builder()
                .teaId(UUID.randomUUID())
                .orderQuantity(quantity)
                .build();
    }
}
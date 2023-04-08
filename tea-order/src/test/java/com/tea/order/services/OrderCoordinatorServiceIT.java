package com.tea.order.services;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.dto.order.TeaOrderLineDto;
import com.tea.common.messaging.event.order.AllocateOrderEvent;
import com.tea.common.messaging.event.order.AllocationResultEvent;
import com.tea.common.messaging.event.order.OrderValidatedEvent;
import com.tea.common.messaging.event.order.ValidateOrderEvent;
import com.tea.order.domain.OrderStatus;
import com.tea.order.domain.TeaOrder;
import com.tea.order.domain.TeaOrderLine;
import com.tea.order.mapper.TeaOrderMapper;
import com.tea.order.repository.TeaOrderRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_PROCESSING_EXCHANGE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_VALIDATION_QUEUE;
import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_VALIDATION_RESULT_ROUTING_KEY;
import static java.util.concurrent.TimeUnit.SECONDS;


@Tag("IT")
@Testcontainers
@SpringBootTest
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
    void coordinatorShouldMoveOrderFromNewToAllocatedStatus_WhenValidationAndAllocationWereSuccessful() {
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
    void coordinatorShouldMoveAllocatedOrderToPickedUpStatus() {
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

    private TeaOrderLineDto orderLine(int quantity) {
        return TeaOrderLineDto.builder()
                .teaId(UUID.randomUUID())
                .orderQuantity(quantity)
                .build();
    }
}
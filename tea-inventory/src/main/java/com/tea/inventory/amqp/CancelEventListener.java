package com.tea.inventory.amqp;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.messaging.event.order.CancelOrderEvent;
import com.tea.inventory.services.CancelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.tea.common.messaging.AmqpOrderProcessingConfig.ORDER_CANCEL_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelEventListener {
    private final CancelService cancelService;

    @Transactional
    @RabbitListener(queues = ORDER_CANCEL_QUEUE)
    public void listen(CancelOrderEvent event) {
        TeaOrderDto orderDto = event.orderDto();
        Objects.requireNonNull(orderDto, "Order in cancel event cant be null") ;
        log.debug("Got cancel event for order[{}]", orderDto.getId());
        cancelService.cancel(orderDto);
    }
}

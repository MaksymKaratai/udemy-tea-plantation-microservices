package com.tea.plantation.services;

import com.tea.common.messaging.event.InventoryRecordEvent;
import com.tea.common.messaging.event.PackingEvent;
import com.tea.common.messaging.AmqpConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackingService {
    private final AmqpTemplate template;

    @RabbitListener(queues = AmqpConfig.PACKAGING_QUEUE)
    public void makeTeaPacking(PackingEvent packingEvent) {
        log.debug("Got packaging event[{}], start packing process", packingEvent);
        try {
            // simulate some work for packaging tea at plantation and sending it to the inventory :)
            Thread.sleep(ThreadLocalRandom.current().nextInt(3000));
            log.debug("Finished packaging, send an event to update inventory");
            var event = new InventoryRecordEvent(packingEvent.teaId(), packingEvent.upc(), packingEvent.amountToPackage());
            template.convertAndSend(AmqpConfig.INVENTORY_QUEUE, event);
        }
        catch (Exception ex) {
            log.error("Unable to make a packaging and notify inventory about it", ex);
            throw new RuntimeException(ex);
        }
    }
}

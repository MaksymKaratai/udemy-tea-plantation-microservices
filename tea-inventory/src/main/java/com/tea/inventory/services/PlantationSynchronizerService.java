package com.tea.inventory.services;

import com.tea.common.dto.event.InventoryRecordEvent;
import com.tea.common.dto.inventory.TeaInventoryDto;
import com.tea.common.messaging.AmqpConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlantationSynchronizerService {
    private final TeaInventoryService inventoryService;

    @RabbitListener(queues = AmqpConfig.INVENTORY_QUEUE)
    public void synchronize(InventoryRecordEvent event) {
        log.debug("Got event for inventory update[{}]", event);
        var dto = TeaInventoryDto.builder()
                .teaId(event.teaId())
                .quantityOnHand(event.quantityOnHand())
                .build();
        TeaInventoryDto created = inventoryService.create(dto);
        log.debug("Finishing inventory update, new record created at[{}] with id[{}] for tea[{}]",
                created.getCreateDate(), created.getId(), created.getTeaId());
    }
}

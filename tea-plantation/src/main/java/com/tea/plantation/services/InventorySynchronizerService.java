package com.tea.plantation.services;

import com.tea.common.messaging.event.PackingEvent;
import com.tea.common.messaging.AmqpConfig;
import com.tea.plantation.domain.Tea;
import com.tea.plantation.repository.TeaRepository;
import com.tea.plantation.services.client.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventorySynchronizerService {
    private final AmqpTemplate template;
    private final TeaRepository teaRepository;
    private final InventoryService inventoryService;

    @Scheduled(fixedRateString = "${tea.plantation.synchronize-with-inventory-duration}")
    public void synchronize() {
        log.debug("Start synchronization with inventory service");
        List<Tea> all = teaRepository.findAll();
        for (var tea : all) {
            try {
                synchronize(tea);
            }
            catch (Exception ex) {
                log.warn("Unable to make synchronization with inventory for tea with uuid[{}] due to [{}]", tea.getUuid(), ex.getMessage());
            }
        }
        log.debug("Finished synchronization with inventory service");
    }

    private void synchronize(Tea tea) {
        UUID teaUuid = tea.getUuid();
        Integer rightNowOnHand = inventoryService.countQuantityOnHandByTeaId(teaUuid.toString());
        if (tea.getMinimalAmountInInventory() <= rightNowOnHand) {
            return;
        }
        log.debug("Inventory has [{}] units of tea[{}], while minimum is [{}]", rightNowOnHand, teaUuid, tea.getMinimalAmountInInventory());
        var packingEvent = new PackingEvent(teaUuid, tea.getUpc(), tea.getQuantityToPackage());
        template.convertAndSend(AmqpConfig.PACKAGING_QUEUE, packingEvent);
        log.debug("Sent packing event for tea[{}]", teaUuid);
    }
}

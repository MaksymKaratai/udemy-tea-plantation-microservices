package com.tea.plantation.services.client;

import com.tea.common.dto.inventory.TeaInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryHttpService implements InventoryService {
    private final InventoryClient client;

    @Override
    public Integer countQuantityOnHandByTeaId(String teaUuid) {
        List<TeaInventoryDto> inventoryRecords = client.getInventoryRecordsById(teaUuid);
        if (CollectionUtils.isEmpty(inventoryRecords)) {
            log.debug("Got empty successful response from inventory for tea with Id[{}]", teaUuid);
            return 0;
        }
        return inventoryRecords.stream()
                .mapToInt(TeaInventoryDto::getQuantityOnHand).sum();
    }
}

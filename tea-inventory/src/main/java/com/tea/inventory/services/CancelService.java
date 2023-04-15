package com.tea.inventory.services;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.dto.order.TeaOrderLineDto;
import com.tea.inventory.domain.TeaInventory;
import com.tea.inventory.repository.TeaInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelService {
    private final TeaInventoryRepository repository;

    @Transactional
    public void cancel(TeaOrderDto orderDto) {
        List<TeaOrderLineDto> orderLines = orderDto.getOrderLines();
        if (CollectionUtils.isEmpty(orderLines)) {
            log.debug("No order lines in order nothing to process");
            return;
        }
        var newItems = new ArrayList<TeaInventory>(orderLines.size());
        for (TeaOrderLineDto line : orderLines) {
            Integer allocated = line.getQuantityAllocated();
            if (allocated == null || allocated <= 0) {
                continue;
            }
            newItems.add(new TeaInventory(null, line.getTeaId(), null, null, null, allocated));
        }
        repository.saveAllAndFlush(newItems);
        log.debug("Created [{}] new inventory records due to order cancel", newItems.size());
    }
}

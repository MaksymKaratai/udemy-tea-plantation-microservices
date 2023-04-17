package com.tea.inventory.services;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.dto.order.TeaOrderLineDto;
import com.tea.inventory.domain.TeaInventory;
import com.tea.inventory.repository.TeaInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Very plain and straightforward implementation for allocation service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationService {
    private final TeaInventoryRepository repository;

    @Transactional
    public boolean allocateOrder(TeaOrderDto orderDto) {
        Objects.requireNonNull(orderDto, "Order dto can't be null");
        log.debug("Start allocation for order[{}]", orderDto.getId());
        List<TeaOrderLineDto> orderLines = orderDto.getOrderLines();
        int leftToAllocateForOrder = 0;
        for (var line : orderLines) {
            int leftToAllocate = tryToAllocateOrderLine(line);
            leftToAllocateForOrder += leftToAllocate;
        }
        log.debug("Allocation finished, left to allocate = [{}]", leftToAllocateForOrder);
        return leftToAllocateForOrder == 0;
    }

    /**
     * @return amount of items left to allocate for particular order line
     */
    private int tryToAllocateOrderLine(TeaOrderLineDto line) {
        int orderQuantity = Objects.requireNonNullElse(line.getOrderQuantity(), 0);
        int alreadyAllocated = Objects.requireNonNullElse(line.getQuantityAllocated(), 0);
        if (orderQuantity - alreadyAllocated == 0) {
            return 0;
        }
        List<TeaInventory> allByTeaId = repository.findAllByTeaId(line.getTeaId());
        for (var inventory : allByTeaId) {
            int leftToAllocate = orderQuantity - alreadyAllocated;
            int onHand = inventory.getQuantityOnHand();
            if (onHand >= leftToAllocate) {//fully cover needed amount
                inventory.setQuantityOnHand(onHand - leftToAllocate);
                line.setQuantityAllocated(orderQuantity);
                dropOrUpdateInventoryRecord(inventory);
                return 0;
            }
            // we can fill line only partially
            alreadyAllocated += onHand;
            inventory.setQuantityOnHand(0);
            dropOrUpdateInventoryRecord(inventory);
        }
        line.setQuantityAllocated(alreadyAllocated);
        return orderQuantity - alreadyAllocated;
    }

    private void dropOrUpdateInventoryRecord(TeaInventory record) {
        if (record.getQuantityOnHand() == 0) {
            repository.delete(record);
            return;
        }
        repository.save(record);
    }
}

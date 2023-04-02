package com.tea.inventory.services;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.dto.order.TeaOrderLineDto;
import com.tea.inventory.domain.TeaInventory;
import com.tea.inventory.repository.TeaInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {
    @Mock
    private TeaInventoryRepository repository;

    private AllocationService allocationService;

    @BeforeEach
    void setUp() {
        allocationService = new AllocationService(repository);
    }

    @Test
    void allocateOrder_givenNullOrderDto_shouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> allocationService.allocateOrder(null));
    }

    @Test
    void allocateOrder_WithPartiallyAllocatedOrderLines_ShouldReturnTrue() {
        UUID id1 = id();
        UUID id2 = id();
        int quantityOrdered1 = 11;
        int quantityOrdered2 = 15;
        TeaOrderLineDto line1 = makeLine(id1, quantityOrdered1, 5);
        TeaOrderLineDto line2 = makeLine(id2, quantityOrdered2, 7);
        var orderDto = TeaOrderDto.builder().orderLines(List.of(line1, line2)).build();

        TeaInventory inventory1 = makeInventory(id1, 6);
        TeaInventory inventory2 = makeInventory(id2, 8);
        when(repository.findAllByTeaId(id1)).thenReturn(List.of(inventory1));
        when(repository.findAllByTeaId(id2)).thenReturn(List.of(inventory2));
        doNothing().when(repository).delete(any(TeaInventory.class));

        boolean result = allocationService.allocateOrder(orderDto);

        assertTrue(result);
        assertEquals(0, inventory1.getQuantityOnHand());
        assertEquals(0, inventory2.getQuantityOnHand());
        assertEquals(quantityOrdered1, line1.getQuantityAllocated());
        assertEquals(quantityOrdered2, line2.getQuantityAllocated());

        verify(repository, times(2)).delete(any(TeaInventory.class));
    }

    @Test
    public void allocateOrder_WithFullyAllocatedOrderLines_ShouldReturnTrue() {
        UUID id1 = id();
        UUID id2 = id();
        TeaOrderLineDto line1 = makeLine(id1, 2, 2);
        TeaOrderLineDto line2 = makeLine(id2, 3, 3);
        TeaOrderDto orderDto = TeaOrderDto.builder().orderLines(List.of(line1, line2)).build();

        boolean result = allocationService.allocateOrder(orderDto);

        assertTrue(result);
        verifyNoInteractions(repository);
    }

    @Test
    public void allocateOrder_WithNotEnoughInventory_ShouldReturnFalse() {
        UUID id = id();
        TeaOrderLineDto line = makeLine(id, 11, 5);
        TeaOrderDto orderDto = TeaOrderDto.builder().orderLines(List.of(line)).build();

        TeaInventory inventory = makeInventory(id, 4);
        when(repository.findAllByTeaId(id)).thenReturn(List.of(inventory));

        boolean allocationResult = allocationService.allocateOrder(orderDto);

        assertFalse(allocationResult);
        assertEquals(9, line.getQuantityAllocated());
        assertEquals(0, inventory.getQuantityOnHand());

        verify(repository, times(1)).delete(inventory);
    }

    @Test
    void allocateOrder_ShouldReturnTrue_WhenSeveralInventoryRecordsNeededForSuccessfulAllocation() {
        UUID id = id();
        TeaOrderLineDto line1 = makeLine(id, 11, 0);
        var orderDto = TeaOrderDto.builder().orderLines(List.of(line1)).build();

        TeaInventory inventory1 = makeInventory(id, 5);
        TeaInventory inventory2 = makeInventory(id, 6);
        when(repository.findAllByTeaId(id)).thenReturn(List.of(inventory1, inventory2));

        boolean result = allocationService.allocateOrder(orderDto);

        assertTrue(result);
        assertEquals(0, inventory1.getQuantityOnHand());
        assertEquals(0, inventory2.getQuantityOnHand());
        assertEquals(11, line1.getQuantityAllocated());

        verify(repository, times(2)).delete(any(TeaInventory.class));
    }

    @Test
    void allocateOrder_ShouldReturnFalse_WhenSeveralInventoryRecordsNotEnoughForSuccessfulAllocation() {
        UUID id = id();
        TeaOrderLineDto line1 = makeLine(id, 20, 0);
        var orderDto = TeaOrderDto.builder().orderLines(List.of(line1)).build();

        TeaInventory inventory1 = makeInventory(id, 5);
        TeaInventory inventory2 = makeInventory(id, 6);
        when(repository.findAllByTeaId(id)).thenReturn(List.of(inventory1, inventory2));

        boolean result = allocationService.allocateOrder(orderDto);

        assertFalse(result);
        assertEquals(0, inventory1.getQuantityOnHand());
        assertEquals(0, inventory2.getQuantityOnHand());
        assertEquals(11, line1.getQuantityAllocated());

        verify(repository, times(2)).delete(any(TeaInventory.class));
    }

    private static TeaOrderLineDto makeLine(UUID id, int quantityOrdered, int quantityAllocated) {
        return TeaOrderLineDto.builder()
                .teaId(id)
                .orderQuantity(quantityOrdered)
                .quantityAllocated(quantityAllocated)
                .build();
    }

    private static TeaInventory makeInventory(UUID id, int quantityOnHand) {
        TeaInventory inventory = new TeaInventory();
        inventory.setTeaId(id);
        inventory.setQuantityOnHand(quantityOnHand);
        return inventory;
    }

    private UUID id() {
        return UUID.randomUUID();
    }
}
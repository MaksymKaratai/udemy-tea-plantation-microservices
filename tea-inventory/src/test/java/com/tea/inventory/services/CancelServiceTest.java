package com.tea.inventory.services;

import com.tea.common.dto.order.TeaOrderDto;
import com.tea.common.dto.order.TeaOrderLineDto;
import com.tea.inventory.domain.TeaInventory;
import com.tea.inventory.repository.TeaInventoryRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CancelServiceTest {
    @Mock
    TeaInventoryRepository repository;

    CancelService cancelService;

    @Captor
    ArgumentCaptor<List<TeaInventory>> captor;

    @BeforeEach
    void setUp() {
        cancelService = new CancelService(repository);
    }

    @Test
    void shouldCreateNewInventoryRecords_ForCanceledOrderLines() {
        UUID teaId1 = UUID.randomUUID();
        UUID teaId2 = UUID.randomUUID();
        int quantity1 = 5;
        int quantity2 = 8;
        var line1 = TeaOrderLineDto.builder()
                .teaId(teaId1)
                .quantityAllocated(quantity1)
                .build();
        var line2 = TeaOrderLineDto.builder()
                .teaId(teaId2)
                .quantityAllocated(quantity2)
                .build();
        var orderDto = TeaOrderDto.builder().orderLines(List.of(line1, line2)).build();

        cancelService.cancel(orderDto);

        Mockito.verify(repository).saveAllAndFlush(captor.capture());

        List<TeaInventory> result = captor.getValue();
        Assertions.assertTrue(CollectionUtils.isNotEmpty(result));
        Assertions.assertEquals(2, result.size());
        TeaInventory first = result.get(0);
        TeaInventory second = result.get(1);
        Assertions.assertEquals(teaId1, first.getTeaId());
        Assertions.assertEquals(quantity1, first.getQuantityOnHand());
        Assertions.assertEquals(teaId2, second.getTeaId());
        Assertions.assertEquals(quantity2, second.getQuantityOnHand());
    }

    @Test
    void shouldSkipOrderLines_DoesntContainsQuantityAllocated() {
        UUID teaId = UUID.randomUUID();
        var line = TeaOrderLineDto.builder()
                .teaId(teaId)
                .quantityAllocated(null)
                .build();
        var orderDto = TeaOrderDto.builder().orderLines(List.of(line)).build();

        cancelService.cancel(orderDto);

        Mockito.verify(repository).saveAllAndFlush(captor.capture());

        List<TeaInventory> result = captor.getValue();
        Assertions.assertTrue(CollectionUtils.isEmpty(result));
    }
}
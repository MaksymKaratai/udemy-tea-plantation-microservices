package com.tea.common.messaging.event.order;

import com.tea.common.dto.order.TeaOrderDto;

public record AllocationResultEvent(
    TeaOrderDto orderDto,
    boolean hasErrors,
    boolean pendingForInventory
) {}

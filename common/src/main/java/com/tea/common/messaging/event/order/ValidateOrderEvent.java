package com.tea.common.messaging.event.order;

import com.tea.common.dto.order.TeaOrderDto;

public record ValidateOrderEvent(
    TeaOrderDto order
) {}

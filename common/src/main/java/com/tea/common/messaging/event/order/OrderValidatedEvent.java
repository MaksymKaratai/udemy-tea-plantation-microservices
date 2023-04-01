package com.tea.common.messaging.event.order;

import java.util.UUID;

public record OrderValidatedEvent(
    UUID orderId,
    boolean isValid
) {}

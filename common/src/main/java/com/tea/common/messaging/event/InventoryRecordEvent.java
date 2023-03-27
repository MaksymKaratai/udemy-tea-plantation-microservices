package com.tea.common.messaging.event;

import java.util.UUID;

public record InventoryRecordEvent(
        UUID teaId,
        String upc,
        Integer quantityOnHand
) {}

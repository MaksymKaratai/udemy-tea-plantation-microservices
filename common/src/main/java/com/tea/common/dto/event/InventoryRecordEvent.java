package com.tea.common.dto.event;

import java.util.UUID;

public record InventoryRecordEvent(
        UUID teaId,
        String upc,
        Integer quantityOnHand
) {}

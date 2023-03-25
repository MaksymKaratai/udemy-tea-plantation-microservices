package com.tea.common.dto.event;

import java.util.UUID;

public record PackingEvent(
        UUID teaId,
        String upc,
        Integer amountToPackage
) {}

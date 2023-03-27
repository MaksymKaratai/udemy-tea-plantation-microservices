package com.tea.common.messaging.event;

import java.util.UUID;

public record PackingEvent(
        UUID teaId,
        String upc,
        Integer amountToPackage
) {}

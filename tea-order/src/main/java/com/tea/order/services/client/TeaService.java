package com.tea.order.services.client;

import com.tea.common.dto.plantation.TeaDto;

import java.util.Optional;
import java.util.UUID;

public interface TeaService {
    Optional<TeaDto> getTeaById(String teaId);
    Optional<TeaDto> getTeaById(UUID teaId);
}

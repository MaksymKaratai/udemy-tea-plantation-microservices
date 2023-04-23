package com.tea.order.services.client;

import com.tea.common.dto.plantation.TeaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeaHttpService implements TeaService {
    private final PlantationClient client;

    @Override
    public Optional<TeaDto> getTeaById(UUID teaId) {
        Objects.requireNonNull(teaId, "Cant make a call for null id");
        return getTeaById(teaId.toString());
    }

    @Override
    public Optional<TeaDto> getTeaById(String teaId) {
        if (StringUtils.isBlank(teaId)) {
            throw new IllegalArgumentException("Cant make a call for a blank argument");
        }

        try {
            ResponseEntity<TeaDto> result = client.getTeaById(teaId);
            if (result.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(result.getBody());
            }
            log.debug("Got unsuccessful response code[{}] and body[{}] trying to get Tea with id[{}]",
                    result.getStatusCode(), result.getBody(), teaId);
        } catch (Exception e) {
            log.debug("Unable to get TeaDto by Id[{}] due to [{}]", teaId, e.getMessage());
        }
        return Optional.empty();
    }
}

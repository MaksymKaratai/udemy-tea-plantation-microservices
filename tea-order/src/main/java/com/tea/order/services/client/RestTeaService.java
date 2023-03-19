package com.tea.order.services.client;

import com.tea.common.dto.plantation.TeaDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RestTeaService implements TeaService {
    private final String api;
    private final RestTemplate template;

    public RestTeaService(@Value("${tea.plantation.host}") String host,
                          @Value("${tea.plantation.endpoint}") String endpoint, RestTemplateBuilder builder) {
        api = host + endpoint;
        template = builder.build();
    }

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
            ResponseEntity<TeaDto> result = template.exchange(api, HttpMethod.GET, null, TeaDto.class, teaId);
            if (result.getStatusCode().is2xxSuccessful()) {
                return Optional.ofNullable(result.getBody());
            }
            log.debug("Got unsuccessful response code[{}] and body[{}] trying to access URI[{}] with id[{}]",
                     result.getStatusCode(), result.getBody(), api, teaId);
        } catch (Exception e) {
            log.debug("Unable to get TeaDto by URI[{}] for Id[{}] due to [{}]", api, teaId, e.getMessage());
        }
        return Optional.empty();
    }
}

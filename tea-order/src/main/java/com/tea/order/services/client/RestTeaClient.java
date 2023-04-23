package com.tea.order.services.client;

import com.tea.common.dto.plantation.TeaDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
@Profile("local")
public class RestTeaClient implements PlantationClient {
    private final String api;
    private final RestTemplate template;

    public RestTeaClient(@Value("${tea.plantation.host}") String host,
                         @Value("${tea.plantation.endpoint}") String endpoint, RestTemplateBuilder builder) {
        api = host + endpoint;
        template = builder.build();
    }

    @Override
    public ResponseEntity<TeaDto> getTeaById(String teaId) {
        return template.exchange(api, HttpMethod.GET, null, TeaDto.class, teaId);
    }
}

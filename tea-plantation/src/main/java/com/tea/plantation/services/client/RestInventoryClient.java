package com.tea.plantation.services.client;

import com.tea.common.dto.inventory.TeaInventoryDto;
import com.tea.common.exception.UnsuccessfulResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@Profile("local")
public class RestInventoryClient implements InventoryClient {
    private final String apiPath;
    private final RestTemplate restTemplate;

    public RestInventoryClient(@Value("${tea.inventory.endpoint}") String endpoint,
                               @Value("${tea.inventory.host}") String serviceHost, RestTemplateBuilder builder) {
        this.apiPath = serviceHost + endpoint;
        this.restTemplate = builder.build();
    }

    @Override
    public List<TeaInventoryDto> getInventoryRecordsById(String teaUuid) {
        var responseType = new ParameterizedTypeReference<List<TeaInventoryDto>>(){};
        ResponseEntity<List<TeaInventoryDto>> result = restTemplate.exchange(apiPath, HttpMethod.GET, null, responseType, teaUuid);
        if (!result.getStatusCode().is2xxSuccessful()) {
            log.error("Got unsuccessful response[{}] with body[{}] by path[{}]", result.getStatusCode(), result.getBody(), apiPath);
            throw new UnsuccessfulResponseException(result);
        }
        return result.getBody();
    }
}

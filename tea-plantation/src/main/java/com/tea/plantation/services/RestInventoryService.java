package com.tea.plantation.services;

import com.tea.common.dto.inventory.TeaInventoryDto;
import com.tea.common.exception.UnsuccessfulResponseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class RestInventoryService implements InventoryService {
    private final String apiPath;
    private final RestTemplate restTemplate;

    public RestInventoryService(@Value("${tea.inventory.endpoint}") String endpoint,
                                @Value("${tea.inventory.host}") String serviceHost, RestTemplateBuilder builder) {
        this.apiPath = serviceHost + endpoint;
        this.restTemplate = builder.build();
    }

    @Override
    public Integer countQuantityOnHandByTeaId(String teaUuid) {
        var responseType = new ParameterizedTypeReference<List<TeaInventoryDto>>(){};
        ResponseEntity<List<TeaInventoryDto>> result = restTemplate.exchange(apiPath, HttpMethod.GET, null, responseType, teaUuid);
        if (!result.getStatusCode().is2xxSuccessful()) {
            log.error("Got unsuccessful response[{}] with body[{}] by path[{}]", result.getStatusCode(), result.getBody(), apiPath);
            throw new UnsuccessfulResponseException(result);
        }
        List<TeaInventoryDto> body = result.getBody();
        if (CollectionUtils.isEmpty(body)) {
            log.debug("Got empty successful response from inventory for tea with Id[{}]", teaUuid);
            return 0;
        }
        return body.stream()
                .mapToInt(TeaInventoryDto::getQuantityOnHand).sum();
    }
}

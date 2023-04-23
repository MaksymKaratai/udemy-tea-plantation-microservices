package com.tea.order.services.client;

import com.tea.common.dto.plantation.TeaDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "${tea.plantation.name}")
public interface PlantationClient {
    @RequestMapping(method = RequestMethod.GET, value = "${tea.plantation.endpoint}")
    ResponseEntity<TeaDto> getTeaById(@PathVariable String teaUuid);
}

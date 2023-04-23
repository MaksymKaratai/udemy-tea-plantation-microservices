package com.tea.plantation.services.client;

import com.tea.common.dto.inventory.TeaInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "${tea.inventory.name}")
public interface InventoryClient {
    @RequestMapping(method = RequestMethod.GET, value = "${tea.inventory.endpoint}")
    List<TeaInventoryDto> getInventoryRecordsById(@PathVariable String teaId);
}

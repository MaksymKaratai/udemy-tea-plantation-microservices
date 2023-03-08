package com.tea.inventory.controller;

import com.tea.inventory.dto.TeaInventoryDto;
import com.tea.inventory.services.TeaInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(TeaInventoryController.INVENTORY_API)
public class TeaInventoryController {
    public static final String INVENTORY_API = "/v1/inventory";

    private final TeaInventoryService inventoryService;

    @GetMapping
    @RequestMapping("/tea/{teaId}")
    public ResponseEntity<?> forTea(@PathVariable UUID teaId) {
        log.debug("Get Inventory for tea[{}]", teaId);
        List<TeaInventoryDto> byTeaId = inventoryService.findAllByTeaId(teaId);
        return ResponseEntity.ok(byTeaId);
    }
}

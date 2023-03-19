package com.tea.plantation.controller;

import com.tea.common.controller.CrudController;
import com.tea.common.utils.PageableUtils;
import com.tea.common.dto.plantation.TeaDto;
import com.tea.plantation.services.TeaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(TeaController.TEA_API)
public class TeaController extends CrudController<TeaDto, String, TeaService> {
    public static final String TEA_API = "/v1/tea";

    public TeaController(TeaService service) {
        super(service);
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                     @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                     @RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "type", required = false) String type,
                                     @RequestParam(value = "withInventory", required = false) Boolean withInventory) {
        Pageable page = PageableUtils.page(pageNumber, pageSize);
        Page<TeaDto> teaDtos = service.listTea(name, type, withInventory, page);
        return ResponseEntity.ok(teaDtos);
    }

    @GetMapping("/uuid/{teaUuid}")
    public ResponseEntity<TeaDto> getByUuid(@PathVariable UUID teaUuid) {
        TeaDto dto = service.findByTeaUuid(teaUuid);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/upc/{teaUpc}")
    public ResponseEntity<TeaDto> getByUuid(@PathVariable String teaUpc) {
        TeaDto dto = service.findByTeaUpc(teaUpc);
        return ResponseEntity.ok(dto);
    }

    @Override
    public String location() {
        return TEA_API;
    }
}

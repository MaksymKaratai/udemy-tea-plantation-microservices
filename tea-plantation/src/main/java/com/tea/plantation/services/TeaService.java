package com.tea.plantation.services;

import com.tea.common.exception.EntityNotFoundException;
import com.tea.common.services.BasicService;
import com.tea.plantation.domain.Tea;
import com.tea.common.dto.plantation.TeaDto;
import com.tea.plantation.mapper.TeaMapper;
import com.tea.plantation.repository.TeaRepository;
import com.tea.plantation.services.client.InventoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class TeaService extends BasicService<Tea, TeaDto, String, TeaRepository> {
    private final InventoryService inventoryService;

    public TeaService(TeaMapper mapper, TeaRepository repository, InventoryService inventoryService) {
        super(mapper, repository);
        this.inventoryService = inventoryService;
    }

    public Page<TeaDto> listTea(String name, String type, Boolean withInventory, Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable can't be null");
        var example = Tea.builder()
                .name(StringUtils.isNotBlank(name) ? name : null)
                .type(StringUtils.isNotBlank(type) ? type : null)
                .build();
        Page<TeaDto> result = repository
                .findAll(Example.of(example), pageable)
                .map(mapper::toDto);
        if (Boolean.TRUE.equals(withInventory)) {
            result.forEach(dto -> dto.setQuantityOnHand(inventoryService.countQuantityOnHandByTeaId(dto.getTeaId())));
        }
        return result;
    }

    public TeaDto findByTeaUuid(UUID uuid) {
        return repository.findByUuid(uuid)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(uuid, "uuid"));
    }

    public TeaDto findByTeaUpc(String upc) {
        return repository.findByUpc(upc)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(upc, "upc"));
    }

    @Override
    public TeaDto create(TeaDto teaDto) {
        teaDto.setTeaId(UUID.randomUUID().toString());
        return super.create(teaDto);
    }
}

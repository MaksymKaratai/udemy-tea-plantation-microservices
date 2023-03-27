package com.tea.order.mapper;

import com.tea.common.dto.plantation.TeaDto;
import com.tea.order.domain.TeaOrderLine;
import com.tea.common.dto.order.TeaOrderLineDto;
import com.tea.order.services.client.TeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

import static com.tea.common.Constants.MAPSTRUCT_DELEGATE;

public abstract class TeaOrderLineMapperDecorator implements TeaOrderLineMapper {
    protected TeaService teaService;
    protected TeaOrderLineMapper delegate;

    @Autowired
    @Qualifier(MAPSTRUCT_DELEGATE)
    public void setDelegate(TeaOrderLineMapper delegate) {
        this.delegate = delegate;
    }

    @Autowired
    public void setTeaService(TeaService teaService) {
        this.teaService = teaService;
    }

    @Override
    public TeaOrderLineDto toDto(TeaOrderLine teaOrderLine) {
        TeaOrderLineDto dto = delegate.toDto(teaOrderLine);
        Optional<TeaDto> teaById = teaService.getTeaById(dto.getTeaId());
        if (teaById.isPresent()) {
            TeaDto tea = teaById.get();
            dto.setTeaName(tea.getName());
            dto.setTeaType(tea.getType());
        }
        return dto;
    }
}

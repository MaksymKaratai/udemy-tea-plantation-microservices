package com.tea.order.mapper;

import com.tea.common.mapper.EntityMapper;
import com.tea.order.domain.TeaOrderLine;
import com.tea.order.dto.TeaOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@DecoratedWith(TeaOrderLineMapperDecorator.class)
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeaOrderLineMapper extends EntityMapper<TeaOrderLine, TeaOrderLineDto> {
    @Override
    @Mapping(target = "teaOrder", ignore = true)
    TeaOrderLine toEntity(TeaOrderLineDto teaOrderLineDto);

    @Override
    @Mapping(target = "teaName", ignore = true)
    @Mapping(target = "teaType", ignore = true)
    TeaOrderLineDto toDto(TeaOrderLine teaOrderLine);
}

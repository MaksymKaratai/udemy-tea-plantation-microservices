package com.tea.order.mapper;

import com.tea.common.mapper.EntityMapper;
import com.tea.order.domain.TeaOrderLine;
import com.tea.order.dto.TeaOrderLineDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeaOrderLineMapper extends EntityMapper<TeaOrderLine, TeaOrderLineDto> {
    @Override
    @Mapping(target = "teaOrder", ignore = true)
    TeaOrderLine toEntity(TeaOrderLineDto teaOrderLineDto);
}

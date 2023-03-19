package com.tea.order.mapper;

import com.tea.common.mapper.EntityMapper;
import com.tea.order.domain.TeaOrder;
import com.tea.order.dto.TeaOrderDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    uses = {TeaOrderLineMapper.class},
    componentModel = MappingConstants.ComponentModel.SPRING
)
@DecoratedWith(TeaOrderMapperDecorator.class)
public interface TeaOrderMapper extends EntityMapper<TeaOrder, TeaOrderDto> {
    @Override
    @Mapping( target = "customerId", source = "customer.id")
    TeaOrderDto toDto(TeaOrder teaOrder);

    @Override
    @Mapping(target = "customer", ignore = true)
    TeaOrder toEntity(TeaOrderDto teaOrderDto);
}

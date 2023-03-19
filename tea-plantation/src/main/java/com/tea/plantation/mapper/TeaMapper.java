package com.tea.plantation.mapper;

import com.tea.common.mapper.EntityMapper;
import com.tea.plantation.domain.Tea;
import com.tea.common.dto.plantation.TeaDto;
import org.mapstruct.Builder;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, builder = @Builder(disableBuilder = true))
public interface TeaMapper extends EntityMapper<Tea, TeaDto> {
    @Override
    @Mapping(target = "teaId", source = "uuid")
    @Mapping(target = "quantityOnHand", ignore = true)
    TeaDto toDto(Tea tea);

    @Override
    @InheritInverseConfiguration
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    Tea toEntity(TeaDto teaDto);
}

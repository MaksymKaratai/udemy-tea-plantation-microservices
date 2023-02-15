package com.tea.plantation.mapper;

import com.tea.plantation.domain.Tea;
import com.tea.plantation.dto.TeaDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeaMapper extends EntityMapper<Tea, TeaDto> {

}

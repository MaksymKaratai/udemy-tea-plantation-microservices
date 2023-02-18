package com.tea.plantation.mapper;

import com.tea.common.mapper.EntityMapper;
import com.tea.plantation.domain.Customer;
import com.tea.plantation.dto.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper extends EntityMapper<Customer, CustomerDto> {
}

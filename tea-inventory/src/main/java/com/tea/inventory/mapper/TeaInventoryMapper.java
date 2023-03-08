package com.tea.inventory.mapper;

import com.tea.common.mapper.EntityMapper;
import com.tea.inventory.domain.TeaInventory;
import com.tea.inventory.dto.TeaInventoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeaInventoryMapper extends EntityMapper<TeaInventory, TeaInventoryDto> { }

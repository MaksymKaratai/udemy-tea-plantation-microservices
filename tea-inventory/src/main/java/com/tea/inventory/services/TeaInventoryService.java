package com.tea.inventory.services;

import com.tea.common.mapper.EntityMapper;
import com.tea.common.services.BasicService;
import com.tea.inventory.domain.TeaInventory;
import com.tea.inventory.dto.TeaInventoryDto;
import com.tea.inventory.repository.TeaInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TeaInventoryService extends BasicService<TeaInventory, TeaInventoryDto, UUID, TeaInventoryRepository> {
    @Autowired
    public TeaInventoryService(EntityMapper<TeaInventory, TeaInventoryDto> mapper, TeaInventoryRepository repository) {
        super(mapper, repository);
    }

    public List<TeaInventoryDto> findAllByTeaId(UUID teaId) {
        List<TeaInventory> allByTeaId = repository.findAllByTeaId(teaId);
        return allByTeaId.stream().map(mapper::toDto).toList();
    }
}

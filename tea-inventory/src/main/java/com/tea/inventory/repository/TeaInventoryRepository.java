package com.tea.inventory.repository;

import com.tea.inventory.domain.TeaInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeaInventoryRepository extends JpaRepository<TeaInventory, UUID> {
    List<TeaInventory> findAllByTeaId(UUID teaId);
}

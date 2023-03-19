package com.tea.order.repository;

import com.tea.order.domain.Customer;
import com.tea.order.domain.TeaOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeaOrderRepository extends JpaRepository<TeaOrder, UUID> {
    Page<TeaOrder> findByCustomer(Customer customer, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"orderLines"})
    Optional<TeaOrder> findById( UUID uuid);
}

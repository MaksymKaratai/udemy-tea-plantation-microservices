package com.tea.order.repository;

import com.tea.order.domain.Customer;
import com.tea.order.domain.TeaOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeaOrderRepository extends JpaRepository<TeaOrder, UUID> {
    Page<TeaOrder> findByCustomer(Customer customer, Pageable pageable);
}

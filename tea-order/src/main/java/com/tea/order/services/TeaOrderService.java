package com.tea.order.services;

import com.tea.common.exception.EntityNotFoundException;
import com.tea.order.domain.Customer;
import com.tea.order.domain.OrderStatus;
import com.tea.order.domain.TeaOrder;
import com.tea.order.dto.TeaOrderDto;
import com.tea.order.mapper.TeaOrderMapper;
import com.tea.order.repository.CustomerRepository;
import com.tea.order.repository.TeaOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeaOrderService {
    private final TeaOrderMapper teaOrderMapper;
    private final TeaOrderRepository teaOrderRepository;
    private final CustomerRepository customerRepository;

    public Page<TeaOrderDto> customerOrders(UUID customerId, Pageable pageable) {
        Customer customer = findCustomer(customerId);
        Page<TeaOrder> customerOrders = teaOrderRepository.findByCustomer(customer, pageable);
        return customerOrders.map(teaOrderMapper::toDto);
    }

    private Customer findCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(customerId));
    }

    @Transactional
    public TeaOrderDto makeOrder(TeaOrderDto orderDto) {
        Customer customer = findCustomer(orderDto.getCustomerId());
        TeaOrder teaOrder = teaOrderMapper.toEntity(orderDto);
        teaOrder.setCustomer(customer);
        TeaOrder saved = teaOrderRepository.saveAndFlush(teaOrder);
        log.debug("Made an order with id[{}]", saved.getId());
        return teaOrderMapper.toDto(saved);
    }

    @Transactional
    public void pickupOrder(UUID orderId) {
        TeaOrder order = teaOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(orderId));
        if (!OrderStatus.READY.equals(order.getOrderStatus())) {
            throw new IllegalStateException("Can't pickup order with state["+ order.getOrderStatus()+"]");
        }
        order.setOrderStatus(OrderStatus.PICKED_UP);
        teaOrderRepository.saveAndFlush(order);
        log.debug("Moved order[{}] to [{}] status", orderId, OrderStatus.PICKED_UP);
    }

    public TeaOrderDto findOrder(UUID orderId) {
        return teaOrderRepository.findById(orderId)
                .map(teaOrderMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(orderId));
    }
}

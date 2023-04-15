package com.tea.order.controller;

import com.tea.common.utils.PageableUtils;
import com.tea.common.dto.order.TeaOrderDto;
import com.tea.order.services.TeaOrderService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(TeaOrderController.ORDER_API)
public class TeaOrderController {
    public static final String ORDER_API = "/v1/order";

    private final TeaOrderService orderService;

    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<?> customerOrders(@PathVariable @NotNull UUID customerId,
                                            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        Pageable page = PageableUtils.page(pageNumber, pageSize);
        Page<TeaOrderDto> orders = orderService.customerOrders(customerId, page);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<TeaOrderDto> getOrder(@PathVariable UUID orderId) {
        TeaOrderDto order = orderService.findOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<TeaOrderDto> makeOrder(@RequestBody @Validated TeaOrderDto orderDto) {
        TeaOrderDto result = orderService.makeOrder(orderDto);
        return ResponseEntity.created(URI.create(ORDER_API + "/" + result.getId())).body(result);
    }

    @PostMapping("/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable @NotNull UUID orderId) {
        orderService.pickupOrder(orderId);
    }

    @PostMapping("/{orderId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable @NotNull UUID orderId) {
        orderService.cancelOrder(orderId);
    }
}

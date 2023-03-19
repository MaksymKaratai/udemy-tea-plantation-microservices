package com.tea.order.dto;

import com.tea.order.domain.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
public class TeaOrderDto extends JpaDto {
    @NotNull
    private UUID customerId;
    private OrderStatus orderStatus;
    @NotEmpty
    private List<TeaOrderLineDto> orderLines;

    @Builder
    public TeaOrderDto(@Null UUID id, @Null Integer version, @Null Instant createDate, @Null Instant updateDate,
                       UUID customerId, OrderStatus orderStatus, List<TeaOrderLineDto> orderLines) {
        super(id, version, createDate, updateDate);
        this.customerId = customerId;
        this.orderStatus = orderStatus;
        this.orderLines = orderLines;
    }
}

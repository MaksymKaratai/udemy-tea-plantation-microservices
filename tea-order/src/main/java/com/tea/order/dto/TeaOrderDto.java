package com.tea.order.dto;

import com.tea.order.domain.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString(callSuper = true)
public class TeaOrderDto extends JpaDto {
    @NotNull
    private UUID customerId;
    private OrderStatus orderStatus;
    @NotEmpty
    private List<TeaOrderLineDto> orderLines;
}

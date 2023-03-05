package com.tea.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString(callSuper = true)
public class TeaOrderLineDto extends JpaDto {
    @NotNull
    private UUID teaId;
    @Positive
    private Integer orderQuantity;
    @Null
    private Integer quantityAllocated;
}

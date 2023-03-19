package com.tea.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
public class TeaOrderLineDto extends JpaDto {
    @NotNull
    private UUID teaId;
    private String teaUpc;
    private String teaName;
    private String teaType;
    @Positive
    private Integer orderQuantity;
    @Null
    private Integer quantityAllocated;

    @Builder
    public TeaOrderLineDto(@Null UUID id, @Null Integer version, @Null Instant createDate, @Null Instant updateDate,
                           UUID teaId, String teaUpc, String teaName, String teaType, Integer orderQuantity, Integer quantityAllocated) {
        super(id, version, createDate, updateDate);
        this.teaId = teaId;
        this.teaUpc = teaUpc;
        this.teaName = teaName;
        this.teaType = teaType;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }
}

package com.tea.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TeaOrderLine extends JpaEntity {
    private UUID teaId;
    private String teaUpc;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TeaOrder teaOrder;

    @Builder
    public TeaOrderLine(UUID id, Integer version, Instant createDate, Instant updateDate, UUID teaId, String teaUpc,
                        Integer orderQuantity, Integer quantityAllocated, TeaOrder teaOrder) {
        super(id, version, createDate, updateDate);
        this.teaId = teaId;
        this.teaUpc = teaUpc;
        this.teaOrder = teaOrder;
        this.orderQuantity = orderQuantity == null ? 0 : orderQuantity;
        this.quantityAllocated = quantityAllocated == null ? 0 : quantityAllocated;
    }
}

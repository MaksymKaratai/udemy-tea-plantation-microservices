package com.tea.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TeaOrderLine extends JpaEntity {
    private UUID teaId;
    @Builder.Default
    private Integer orderQuantity = 0;
    @Builder.Default
    private Integer quantityAllocated = 0;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TeaOrder teaOrder;
}

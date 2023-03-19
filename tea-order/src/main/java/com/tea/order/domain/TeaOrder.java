package com.tea.order.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TeaOrder extends JpaEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private OrderStatus orderStatus = OrderStatus.NEW;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "teaOrder", cascade = CascadeType.ALL)
    private List<TeaOrderLine> orderLines;

    @Builder
    public TeaOrder(UUID id, Integer version, Instant createDate, Instant updateDate, Customer customer,
                    OrderStatus orderStatus, List<TeaOrderLine> orderLines) {
        super(id, version, createDate, updateDate);
        this.customer = customer;
        this.orderStatus = orderStatus == null ? OrderStatus.NEW : orderStatus;
        this.orderLines = orderLines;
    }
}

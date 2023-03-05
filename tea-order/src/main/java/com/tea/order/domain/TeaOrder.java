package com.tea.order.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TeaOrder extends JpaEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.NEW;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "teaOrder", cascade = CascadeType.ALL)
    private List<TeaOrderLine> orderLines;
}

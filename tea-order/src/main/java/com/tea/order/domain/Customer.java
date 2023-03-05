package com.tea.order.domain;

import jakarta.persistence.Entity;
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
public class Customer extends JpaEntity {

    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "customer")
    private List<TeaOrder> orders;
}

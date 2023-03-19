package com.tea.inventory.domain;

import com.tea.common.domain.Identifiable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TeaInventory implements Identifiable<UUID> {
    @Id
    @UuidGenerator
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @EqualsAndHashCode.Include
    private UUID teaId;

    @Version
    @EqualsAndHashCode.Include
    private Integer version;

    @CreatedDate
    private Instant createDate;

    @LastModifiedDate
    private Instant updateDate;

    private Integer quantityOnHand;
}

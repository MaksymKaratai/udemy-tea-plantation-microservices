package com.tea.order.domain;

import com.tea.common.domain.Identifiable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class JpaEntity implements Identifiable<UUID> {
    @Id
    @UuidGenerator
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    @Version
    @EqualsAndHashCode.Include
    private Integer version;

    @CreatedDate
    private Instant createDate;

    @LastModifiedDate
    private Instant updateDate;
}

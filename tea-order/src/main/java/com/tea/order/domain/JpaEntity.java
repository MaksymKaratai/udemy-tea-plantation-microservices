package com.tea.order.domain;

import com.tea.common.domain.Identifiable;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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

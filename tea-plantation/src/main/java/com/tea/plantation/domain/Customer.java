package com.tea.plantation.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Identifiable<String> {
    @Id
    private String id;
    private String name;
    @CreatedDate
    private Instant createDate;
    @LastModifiedDate
    private Instant updateDate;
    @Version
    private Integer version;
}

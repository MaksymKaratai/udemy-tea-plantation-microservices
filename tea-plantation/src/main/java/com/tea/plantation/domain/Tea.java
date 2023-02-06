package com.tea.plantation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class Tea implements Identifiable<String> {
    @Id
    private String id;
    private String name;
    private String type;
    private Long upc;
    @CreatedDate
    private Instant createDate;
    @LastModifiedDate
    private Instant updateDate;
    @Version
    private Integer version;
}

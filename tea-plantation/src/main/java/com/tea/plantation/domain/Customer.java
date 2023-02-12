package com.tea.plantation.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = Customer.MONGO_COLLECTION)
public class Customer extends UUIDEntity implements Identifiable<String> {
    public static final String MONGO_COLLECTION = "customer";
    @Id
    private String id;
    private String name;
}

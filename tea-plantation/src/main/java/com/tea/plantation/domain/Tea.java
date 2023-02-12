package com.tea.plantation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = Tea.MONGO_COLLECTION)
public class Tea extends UUIDEntity implements Identifiable<String> {
    public static final String MONGO_COLLECTION = "tea";
    @Id
    private String id;
    private String name;
    private String type;
}

package com.tea.plantation.domain;

import com.tea.common.domain.Identifiable;
import com.tea.common.domain.UUIDEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = Tea.MONGO_COLLECTION)
public class Tea extends UUIDEntity implements Identifiable<String> {
    public static final String MONGO_COLLECTION = "tea";
    public static final String UPC_INDEX = "upcIndex";
    public static final String UUID_INDEX = "uuidIndex";

    @Id
    private String id;
    private String name;
    private String type;

    @Indexed(unique = true, name = UPC_INDEX)
    private String upc;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;
}

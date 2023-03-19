package com.tea.common.dto.plantation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tea.common.domain.Identifiable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class TeaDto implements Identifiable<String> {
    @Null
    private String id;
    @Null // plantation service generate uuid for tea
    private String teaId;
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotBlank
    private String upc;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Positive
    @NotNull
    private BigDecimal price;

    @Null // available from inventory service
    private Integer quantityOnHand;
}

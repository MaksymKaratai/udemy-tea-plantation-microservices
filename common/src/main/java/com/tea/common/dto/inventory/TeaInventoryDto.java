package com.tea.common.dto.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tea.common.Constants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TeaInventoryDto {
    @Null
    private UUID id;

    @NotNull
    private UUID teaId;

    @Null
    private Integer version;

    @Null
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = Constants.UTC_TIMEZONE, shape = JsonFormat.Shape.STRING)
    private Instant createDate;

    @Null
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = Constants.UTC_TIMEZONE, shape = JsonFormat.Shape.STRING)
    private Instant updateDate;

    @NotNull
    private Integer quantityOnHand;
}

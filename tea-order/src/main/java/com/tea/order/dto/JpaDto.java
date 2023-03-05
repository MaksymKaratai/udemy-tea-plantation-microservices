package com.tea.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tea.common.Constants;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class JpaDto {
    @Null
    private UUID id;

    @Null
    private Integer version;

    @Null
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = Constants.UTC_TIMEZONE, shape = JsonFormat.Shape.STRING)
    private Instant createDate;

    @Null
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = Constants.UTC_TIMEZONE, shape = JsonFormat.Shape.STRING)
    private Instant updateDate;
}

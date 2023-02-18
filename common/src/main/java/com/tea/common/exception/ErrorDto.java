package com.tea.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tea.common.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.Instant;

@Setter
@Getter
@ToString
public class ErrorDto {
    private String path;
    private String message;
    private String rootCauseMessage;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern= Constants.DATE_TIME_FORMAT, timezone = Constants.UTC_TIMEZONE)
    private Instant timestamp = Instant.now();

    public ErrorDto(Exception ex) {
        this.message = ex.getMessage();
        this.rootCauseMessage = ExceptionUtils.getRootCauseMessage(ex);
    }

    public ErrorDto(Exception ex, HttpServletRequest request) {
        this.path = request.getRequestURI();
        this.message = ex.getMessage();
        this.rootCauseMessage = ExceptionUtils.getRootCauseMessage(ex);
    }
}

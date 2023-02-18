package com.tea.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CommonControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex, request));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (request instanceof ServletWebRequest servletReq) {
            HttpServletRequest httpServletReq = servletReq.getRequest();
            return ResponseEntity.badRequest().body(new ErrorDto(ex, httpServletReq));
        }
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }
}

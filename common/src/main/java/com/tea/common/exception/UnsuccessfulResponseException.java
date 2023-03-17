package com.tea.common.exception;

import org.springframework.http.ResponseEntity;

public class UnsuccessfulResponseException extends RuntimeException {
    public UnsuccessfulResponseException(ResponseEntity<?> response) {
        super("Got unsuccessful response with code [" + response.getStatusCode() + "] and body [" + response.getBody() + "]");
    }
}

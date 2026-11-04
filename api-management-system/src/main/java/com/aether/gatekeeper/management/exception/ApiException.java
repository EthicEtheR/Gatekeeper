package com.aether.gatekeeper.management.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private  HttpStatus status;
    public ApiException(String message,HttpStatus status) {
        super(message);
        this.status=status;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getStatus() {
        return status;
    }
}

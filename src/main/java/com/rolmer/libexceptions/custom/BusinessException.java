package com.rolmer.libexceptions.custom;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final String status;
    private final HttpStatus httpStatus;

    public BusinessException(String status, String message, HttpStatus httpStatus) {
        super(message);
        this.status = status;
        this.httpStatus = httpStatus;
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException("BAD_REQUEST", message, HttpStatus.BAD_REQUEST);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }

    // ... outros métodos factory conforme necessidade

    public String getStatus() { return status; }
    public HttpStatus getHttpStatus() { return httpStatus; }
}
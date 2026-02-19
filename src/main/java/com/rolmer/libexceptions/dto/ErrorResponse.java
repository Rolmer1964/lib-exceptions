package com.rolmer.libexceptions.dto;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String code;           // Código do erro (ex: "BUSINESS_ERROR", "VALIDATION_ERROR", etc)
    private String message;        // Mensagem amigável para o usuário
    private String details;        // Detalhes técnicos (opcional, pode ser null)
    private LocalDateTime timestamp; // Momento em que o erro ocorreu
    private String path;           // Endpoint ou recurso onde ocorreu o erro (opcional)

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String code, String message, String details, String path) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

    // Getters e setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

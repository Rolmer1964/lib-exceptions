package com.rolmer.libexceptions.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ErrorDetails {

    private LocalDateTime timestamp;
    private String type;
    private String internalCode;
    private String message;
    private String path;
    private List<ErrorForm> fields;
    private List<String> details;
    private HttpStatus status;




}

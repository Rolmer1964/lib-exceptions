package com.rolmer.libexceptions.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ErrorForm {
    private String field;
    private String message;
}

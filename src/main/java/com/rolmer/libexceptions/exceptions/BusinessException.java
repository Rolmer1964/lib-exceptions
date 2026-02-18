package com.rolmer.libexceptions.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@Builder
public class BusinessException extends RuntimeException{
    private final HttpStatus httpStatus;

}

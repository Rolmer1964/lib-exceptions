package com.rolmer.libexceptions.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends Exception{

    private HttpStatus httpStatus;
    private String jsonResponse;

}

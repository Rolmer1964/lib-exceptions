package com.rolmer.libexceptions.exceptions;

import com.rolmer.libexceptions.dto.ErrorForm;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BadRequestException extends Exception {
    private String internalErrorMessage;
    private List<ErrorForm> errorFormList;
    private String externalErrorMessage;

}

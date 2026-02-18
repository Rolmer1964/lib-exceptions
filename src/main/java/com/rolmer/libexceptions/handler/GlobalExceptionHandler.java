package com.rolmer.libexceptions.handler;

import com.rolmer.libexceptions.dto.ErrorDetails;
import com.rolmer.libexceptions.dto.ErrorForm;
import com.rolmer.libexceptions.exceptions.BadRequestException;
import com.rolmer.libexceptions.exceptions.BusinessException;
import com.rolmer.libexceptions.exceptions.ResourceNotFoundException;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {


    //Captura erros de validação de @Valid em @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(buildError(ex, request));
    }

    //Quando o corpo da requisição está mal formado (JSON inváliodo, por exemplo)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(HttpMessageNotReadableException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(buildError(ex, request));
    }

    //Captura BindException (usado com @ModelAttribuite, por exemplo)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorDetails> handleBindException(BindException ex, WebRequest request ) {
        Map<String, String> errors = new HashMap<>();
        return ResponseEntity.badRequest().body(buildError(ex, request));
    }

    //Parâmetro presente mas com valor invalido
    //Exemplo: @RequestParam @Min(18) Integer idade -> idade = 10
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(HttpMessageNotReadableException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(buildError(ex, request));
    }

    //Parâmetro obrigatório, não informado na requisição
    //Exemplo: @RequestParam @NotBlank Seting nome ->  sem o parâmetro nome na request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDetails> handleMissingServletRequestParameterException(HttpMessageNotReadableException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(buildError(ex, request));
    }

    //Uma Falha relacionada a regra de negócio da aplicação
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> handleBusinessException(HttpMessageNotReadableException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(buildError(ex, request));
    }

    //O recurso não foi encontrado (exemplo: json retornou vazio)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(HttpMessageNotReadableException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(buildError(ex, request));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorDetails> handleFeignException(FeignException ex, WebRequest request) {
        return ResponseEntity
                .status(ex.status() > 0 ? ex.status() : 502)
                .body(buildFeignError(ex, request));
    }

    private ErrorDetails buildError(Exception ex, WebRequest request) {
        List<ErrorForm> errorFormList = new ArrayList<>();
        String customMessage = ex.getMessage();

        if (ex instanceof BadRequestException) {
            errorFormList = ((BadRequestException) ex).getErrorFormList();
        } else if (ex instanceof ConstraintViolationException) {
            customMessage = "Erro de validação em um ou mais parâmetros da query. Veja a lista para detalhes";
            ConstraintViolationException constraintEx = (ConstraintViolationException) ex;
            errorFormList = constraintEx.getConstraintViolations()
                    .stream()
                    .map(
                            validation -> ErrorForm.builder()
                                    .field(validation.getPropertyPath().toString())
                                    .message(validation.getMessage())
                                    .build()
                    ).collect(Collectors.toList());
        } else if (ex instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException missingEx = (MissingServletRequestParameterException) ex;
            ErrorForm errorForm = ErrorForm.builder()
                    .field(missingEx.getParameterName())
                    .message(String.format("O parâmetro '%s' é obrigatório", missingEx.getParameterName().toString()))
                    .build();
            errorFormList.add(errorForm);
        } else if (ex instanceof HttpMessageNotReadableException) {
            ErrorForm errorForm = ErrorForm.builder()
                    .field("Request Body")
                    .message("Corpo da requisição inválido ou mal formatado")
                    .build();
            errorFormList.add(errorForm);
        } else if (ex instanceof MethodArgumentNotValidException) {
            customMessage = "Erro de validação de um ou mais campos do body. Veja a lista para detalhes";
            errorFormList = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors()
                    .stream()
                    .map(
                            fieldError -> ErrorForm.builder()
                                    .field(fieldError.getField())
                                    .message(fieldError.getDefaultMessage())
                                    .build()
                    ).collect(Collectors.toList());
        } else if (ex instanceof BindException be) {
            customMessage = "Ocorreu um erro de binding";
            errorFormList = be.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(
                            fieldError -> ErrorForm.builder()
                                    .field(fieldError.getField())
                                    .message(fieldError.getDefaultMessage())
                                    .build()
                    ).collect(Collectors.toList());
        }else if(ex instanceof BusinessException bse) {
            customMessage = bse.getMessage();
            errorFormList = List.of(ErrorForm.builder()
                            .field("Campo não especificado")
                            .message(bse.getMessage())
                    .build());
        }else if(ex instanceof ResourceNotFoundException rnf){
            customMessage = rnf.getMessage();
            errorFormList = List.of(ErrorForm.builder()
                    .field("Campo não especificado")
                    .message(rnf.getMessage())
                    .build());
        }


        return ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .type(ex.getClass().getTypeName())
                .message(customMessage)
                .fields(errorFormList)
                .path(request.getDescription(java.lang.Boolean.FALSE))
                .build();

    }


    private ErrorDetails buildFeignError(FeignException ex, WebRequest request) {
        String responseBody = ex.contentUTF8();
        List<ErrorForm> errorFormList = new ArrayList<>();
        String customMessage = "Erro ao consumir serviço remoto";

        // Tenta extrair detalhes do corpo da resposta (assumindo JSON)
        if (responseBody != null && !responseBody.isBlank()) {
            // Exemplo: tenta mapear para ErrorDetails ou ErrorForm se a API remota seguir padrão semelhante
            try {
                ObjectMapper  mapper = new ObjectMapper();
                // Tenta mapear para seu modelo de erro padrão
                ErrorDetails remoteError = mapper.readValue(responseBody, ErrorDetails.class);
                customMessage = remoteError.getMessage();
                errorFormList = remoteError.getFields();
            } catch (Exception e) {
                // Se não conseguir mapear, adiciona o corpo como mensagem bruta
                errorFormList.add(ErrorForm.builder()
                        .field("feign.response")
                        .message(responseBody)
                        .build());
            }
        } else {
            errorFormList.add(ErrorForm.builder()
                    .field("feign.status")
                    .message("Status: " + ex.status())
                    .build());
        }

        return ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .type(ex.getClass().getTypeName())
                .message(customMessage)
                .fields(errorFormList)
                .path(request.getDescription(false))
                .build();
    }
}

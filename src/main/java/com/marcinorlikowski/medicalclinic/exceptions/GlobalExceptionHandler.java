package com.marcinorlikowski.medicalclinic.exceptions;

import com.marcinorlikowski.medicalclinic.dto.ErrorDto;
import com.marcinorlikowski.medicalclinic.dto.FieldValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({
            MedicalClinicException.class
    })
    public ResponseEntity<ErrorDto> handleMedicalClinicExceptions(MedicalClinicException ex, WebRequest request) {
        ErrorDto errorDto = new ErrorDto(
                getErrorCodeAndName(ex.getStatus()),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false),
                Collections.emptyList()
        );

        log.error(ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(errorDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<FieldValidationErrorDto> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldValidationErrorDto(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        log.error("Validation failed: {}", fields);

        ErrorDto errorDto = new ErrorDto(
                getErrorCodeAndName(HttpStatus.BAD_REQUEST),
                "One or more fields have validation errors",
                LocalDateTime.now(),
                request.getDescription(false),
                fields
        );
        return ResponseEntity.status(ex.getStatusCode()).body(errorDto);
    }

    private String getErrorCodeAndName(HttpStatus httpStatus) {
        return String.format("%d %s", httpStatus.value(), httpStatus.name());
    }
}

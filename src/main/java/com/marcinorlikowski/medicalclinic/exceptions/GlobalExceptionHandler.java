package com.marcinorlikowski.medicalclinic.exceptions;

import com.marcinorlikowski.medicalclinic.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            MedicalClinicException.class
    })
    public ErrorDto handleMedicalClinicExceptions(MedicalClinicException ex, WebRequest request) {
        return new ErrorDto(
                getErrorCodeAndName(ex.getStatus()),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false)
        );
    }

    private String getErrorCodeAndName(HttpStatus httpStatus) {
        return String.format("%d %s", httpStatus.value(), httpStatus.name());
    }
}

package com.marcinorlikowski.medicalclinic.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public abstract class MedicalClinicException extends RuntimeException {
    private String message;
    private HttpStatus status;
}

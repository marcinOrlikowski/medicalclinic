package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends MedicalClinicException {
    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}

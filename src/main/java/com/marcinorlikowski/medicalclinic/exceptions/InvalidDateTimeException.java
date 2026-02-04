package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDateTimeException extends MedicalClinicException {
    public InvalidDateTimeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

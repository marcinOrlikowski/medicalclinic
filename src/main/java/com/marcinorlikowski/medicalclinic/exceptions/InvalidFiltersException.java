package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidFiltersException extends MedicalClinicException {
    public InvalidFiltersException() {
        super("Invalid filters combination", HttpStatus.BAD_REQUEST);
    }
}

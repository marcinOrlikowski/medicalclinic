package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class InstitutionNotFoundException extends MedicalClinicException {
    public InstitutionNotFoundException() {
        super("ERROR - Institution not found", HttpStatus.NOT_FOUND);
    }
}

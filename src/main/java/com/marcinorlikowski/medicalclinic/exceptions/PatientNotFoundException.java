package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException() {
        super("Patient not found", HttpStatus.NOT_FOUND);
    }
}

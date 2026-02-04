package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException() {
        super("ERROR - Doctor not found", HttpStatus.NOT_FOUND);
    }
}

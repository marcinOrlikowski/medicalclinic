package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class PatientAlreadyAssignedException extends MedicalClinicException {
    public PatientAlreadyAssignedException() {
        super("This appointment is already taken", HttpStatus.CONFLICT);
    }
}

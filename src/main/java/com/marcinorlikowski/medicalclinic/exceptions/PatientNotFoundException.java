package com.marcinorlikowski.medicalclinic.exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException() {
        super("ERROR - Patient not found");
    }
}

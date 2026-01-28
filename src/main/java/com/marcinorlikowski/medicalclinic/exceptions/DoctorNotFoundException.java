package com.marcinorlikowski.medicalclinic.exceptions;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException() {
        super("ERROR - Doctor not found");
    }
}

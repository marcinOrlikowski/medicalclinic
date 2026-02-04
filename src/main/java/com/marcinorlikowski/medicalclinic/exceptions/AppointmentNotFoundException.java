package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class AppointmentNotFoundException extends MedicalClinicException {
    public AppointmentNotFoundException() {
        super("ERROR - Appointment not found", HttpStatus.NOT_FOUND);
    }
}

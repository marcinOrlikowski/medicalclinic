package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class AppointmentNotFoundException extends MedicalClinicException {
    public AppointmentNotFoundException() {
        super("Appointment not found", HttpStatus.NOT_FOUND);
    }
}

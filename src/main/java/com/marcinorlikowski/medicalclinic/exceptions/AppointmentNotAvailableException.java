package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class AppointmentNotAvailableException extends MedicalClinicException {
    public AppointmentNotAvailableException() {
        super("ERROR - Appointment is not available anymore", HttpStatus.CONFLICT);
    }
}

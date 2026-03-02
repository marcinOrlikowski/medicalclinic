package com.marcinorlikowski.medicalclinic.exceptions;

import org.springframework.http.HttpStatus;

public class AppointmentNotAvailableException extends MedicalClinicException {
    public AppointmentNotAvailableException() {
        super("Appointment overlaps", HttpStatus.CONFLICT);
    }
}

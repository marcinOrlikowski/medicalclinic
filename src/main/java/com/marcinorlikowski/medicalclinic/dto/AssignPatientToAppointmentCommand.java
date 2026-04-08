package com.marcinorlikowski.medicalclinic.dto;

public record AssignPatientToAppointmentCommand(
        Long appointmentId,
        Long patientId
) {
}

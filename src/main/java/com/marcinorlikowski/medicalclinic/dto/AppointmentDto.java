package com.marcinorlikowski.medicalclinic.dto;

import java.time.LocalDateTime;

public record AppointmentDto(
        Long id,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long doctorId,
        Long patientId
) {
}

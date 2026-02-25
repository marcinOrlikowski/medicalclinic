package com.marcinorlikowski.medicalclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAppointmentCommand(
        @NotNull(message = "start date cannot be empty")
        @Future(message = "start date must be in future")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startDate,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endDate,
        @NotNull(message = "doctor id cannot be empty")
        Long doctorId
) {
}

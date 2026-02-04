package com.marcinorlikowski.medicalclinic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAppointmentCommand(
        @NotNull
        @Future
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startDate,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endDate,
        @NotNull
        Long doctorId
) {
}

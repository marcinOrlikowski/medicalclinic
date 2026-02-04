package com.marcinorlikowski.medicalclinic.dto;

import java.time.LocalDateTime;

public record ErrorDto(
        String httpStatus,
        String message,
        LocalDateTime time,
        String path
) {
}

package com.marcinorlikowski.medicalclinic.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorDto(
        String httpStatus,
        String message,
        LocalDateTime time,
        String path,
        List<FieldValidationErrorDto> fieldErrors
) {
}

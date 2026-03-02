package com.marcinorlikowski.medicalclinic.dto;

public record FieldValidationErrorDto(
        String field,
        String message
) {
}

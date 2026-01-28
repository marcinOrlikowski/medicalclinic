package com.marcinorlikowski.medicalclinic.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreatePatientCommand(
        @NotBlank String email,
        @NotBlank String password,
        String idCardNo,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phoneNumber,
        LocalDate birthDate) {
}

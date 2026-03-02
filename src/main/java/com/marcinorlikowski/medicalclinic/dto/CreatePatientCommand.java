package com.marcinorlikowski.medicalclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CreatePatientCommand(
        @NotBlank(message = "email cannot be empty")
        String email,
        @NotBlank(message = "password cannot be empty")
        String password,
        String idCardNo,
        @NotBlank(message = "first name cannot be empty")
        String firstName,
        @NotBlank(message = "last name cannot be empty")
        String lastName,
        String phoneNumber,
        @Past(message = "birth date must be in past")
        LocalDate birthDate) {
}

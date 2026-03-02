package com.marcinorlikowski.medicalclinic.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDoctorCommand(
        @NotBlank(message = "email cannot be empty")
        String email,
        @NotBlank(message = "password cannot be empty")
        String password,
        @NotBlank(message = "first name cannot be empty")
        String firstName,
        @NotBlank(message = "last name cannot be empty")
        String lastName,
        @NotBlank(message = "specialization cannot be empty")
        String specialization) {
}

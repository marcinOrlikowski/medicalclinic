package com.marcinorlikowski.medicalclinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordCommand(
        @NotBlank(message = "password cannot be empty")
        @Size(min = 8, max = 16)
        String password) {
}

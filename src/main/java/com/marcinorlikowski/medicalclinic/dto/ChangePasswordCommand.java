package com.marcinorlikowski.medicalclinic.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordCommand(@NotBlank String password) {
}

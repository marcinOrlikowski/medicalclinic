package com.marcinorlikowski.medicalclinic.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateInstitutionCommand(
        @NotBlank String name,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String streetName,
        @NotBlank String buildingNumber
) {
}

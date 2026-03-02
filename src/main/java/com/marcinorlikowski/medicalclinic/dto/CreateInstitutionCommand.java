package com.marcinorlikowski.medicalclinic.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateInstitutionCommand(
        @NotBlank(message = "name cannot be empty")
        String name,
        @NotBlank(message = "city cannot be empty")
        String city,
        @NotBlank(message = "postal code cannot be empty")
        String postalCode,
        @NotBlank(message = "street name cannot be empty")
        String streetName,
        @NotBlank(message = "building number cannot be empty")
        String buildingNumber
) {
}

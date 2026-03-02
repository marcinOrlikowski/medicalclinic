package com.marcinorlikowski.medicalclinic.dto;

import java.util.List;

public record InstitutionDto(
        Long id,
        String name,
        String city,
        String postalCode,
        String streetName,
        String buildingNumber,
        List<DoctorDto> doctors) {
}

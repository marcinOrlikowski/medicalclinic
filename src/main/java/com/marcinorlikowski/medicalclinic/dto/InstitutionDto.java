package com.marcinorlikowski.medicalclinic.dto;

import java.util.List;

public record InstitutionDto(String name,
                             String city,
                             String postalCode,
                             String streetName,
                             String buildingNumber,
                             List<DoctorDto> doctors) {
}

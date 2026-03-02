package com.marcinorlikowski.medicalclinic.dto;

import com.marcinorlikowski.medicalclinic.model.Specialization;

public record DoctorDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Specialization specialization) {
}

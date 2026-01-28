package com.marcinorlikowski.medicalclinic.dto;

import com.marcinorlikowski.medicalclinic.model.Specialization;

public record DoctorDto(String email,
                        String firstName,
                        String lastName,
                        Specialization specialization) {
}

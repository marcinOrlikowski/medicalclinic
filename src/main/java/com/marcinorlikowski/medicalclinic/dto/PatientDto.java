package com.marcinorlikowski.medicalclinic.dto;

import java.time.LocalDate;


public record PatientDto(String email,
                         String idCardNo,
                         String firstName,
                         String lastName,
                         String phoneNumber,
                         LocalDate birthDate) {
}

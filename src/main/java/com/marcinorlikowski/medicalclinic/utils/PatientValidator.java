package com.marcinorlikowski.medicalclinic.utils;

import com.marcinorlikowski.medicalclinic.model.Patient;
import com.marcinorlikowski.medicalclinic.repository.PatientRepository;

import java.util.Objects;
import java.util.Optional;

public final class PatientValidator {

    private PatientValidator() {
        throw new UnsupportedOperationException("Cannot create util class");
    }

    public static void validatePatient(Patient patient) {
        Objects.requireNonNull(patient, "Patient cannot be null");
        validateEmail(patient.getEmail());
    }

    public static void validateIfPatientAlreadyExists(PatientRepository repository, Patient patient) {
        validatePatient(patient);
        Optional<Patient> foundPatient = repository.findByEmail(patient.getEmail());
        if (foundPatient.isPresent()) {
            throw new IllegalArgumentException("Patient with this email already exists");
        }
    }

    public static void validateEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }
}

package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.model.Patient;
import com.marcinorlikowski.medicalclinic.repository.PatientRepository;
import com.marcinorlikowski.medicalclinic.utils.PatientValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAll() {
        return patientRepository.getAll();
    }

    public Patient getPatientByEmail(String email) {
        PatientValidator.validateEmail(email);
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No patient with this email"));
    }

    public Patient addPatient(Patient patient) {
        PatientValidator.validatePatient(patient);
        PatientValidator.validateIfPatientAlreadyExists(patientRepository, patient);
        return patientRepository.addPatient(patient);
    }

    public Patient removePatientByEmail(String email) {
        PatientValidator.validateEmail(email);
        Patient foundPatient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No patient with this email"));
        return patientRepository.removePatient(foundPatient);
    }

    public Patient updatePatient(Patient updatedPatient, String email) {
        PatientValidator.validatePatient(updatedPatient);
        PatientValidator.validateEmail(email);
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No patient with this email"));
        patient.updatePatient(updatedPatient);
        return patient;
    }
}

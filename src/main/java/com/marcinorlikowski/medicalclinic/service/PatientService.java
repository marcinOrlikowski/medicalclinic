package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.mapper.PatientMapper;
import com.marcinorlikowski.medicalclinic.model.CreatePatientCommand;
import com.marcinorlikowski.medicalclinic.model.Patient;
import com.marcinorlikowski.medicalclinic.model.PatientDto;
import com.marcinorlikowski.medicalclinic.repository.PatientRepository;
import com.marcinorlikowski.medicalclinic.utils.PatientValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientMapper mapper;
    private final PatientRepository patientRepository;

    public List<PatientDto> getAll() {
        List<Patient> patients = patientRepository.findAll();
        return mapper.toDto(patients);
    }

    public PatientDto getPatientByEmail(String email) {
        PatientValidator.validateEmail(email);
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No patient with this email"));
        return mapper.toDto(patient);
    }

    public List<PatientDto> getPatientsByLastName(String lastName) {
        PatientValidator.validateLastName(lastName);
        return patientRepository.findByLastNameStartingWithIgnoreCase(lastName).stream()
                .map(mapper::toDto)
                .toList();
    }

    public PatientDto addPatient(CreatePatientCommand command) {
        Patient patient = new Patient(command);
        PatientValidator.validatePatient(patient);
        PatientValidator.validateIfPatientAlreadyExists(patientRepository, patient);
        Patient added = patientRepository.save(patient);
        return mapper.toDto(added);
    }

    public void removePatientByEmail(String email) {
        PatientValidator.validateEmail(email);
        Patient foundPatient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No patient with this email"));
        patientRepository.delete(foundPatient);
    }

    public PatientDto updatePatient(CreatePatientCommand command, String email) {
        PatientValidator.validateEmail(email);
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No patient with this email"));
        Patient updatedPatient = new Patient(command);
        PatientValidator.validatePatient(updatedPatient);
        patient.updatePatient(updatedPatient);
        patientRepository.save(patient);
        return mapper.toDto(patient);
    }

    public PatientDto changePatientPassword(String email, String password) {
        PatientValidator.validateEmail(email);
        PatientValidator.validatePassword(password);
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No patient with this email"));
        patient.setPassword(password);
        patientRepository.save(patient);
        return mapper.toDto(patient);
    }
}

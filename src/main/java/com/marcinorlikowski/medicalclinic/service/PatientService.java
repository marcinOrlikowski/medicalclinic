package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.dto.CreatePatientCommand;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.dto.PageMetadata;
import com.marcinorlikowski.medicalclinic.dto.PatientDto;
import com.marcinorlikowski.medicalclinic.exceptions.PatientNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.ResourceAlreadyExistsException;
import com.marcinorlikowski.medicalclinic.mapper.PatientMapper;
import com.marcinorlikowski.medicalclinic.model.*;
import com.marcinorlikowski.medicalclinic.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {
    private final PatientMapper mapper;
    private final PatientRepository patientRepository;
    private final UserService userService;

    public PageDto<PatientDto> getAll(Pageable pageable) {
        log.info("Getting patients page: {}, with {} elements", pageable.getPageNumber(), pageable.getPageSize());
        Page<Patient> patients = patientRepository.findAll(pageable);
        List<PatientDto> patientsDto = mapper.toDto(patients.getContent());
        PageMetadata metadata = new PageMetadata(
                patients.getNumber(),
                patients.getSize(),
                patients.getTotalElements(),
                patients.getTotalPages()
        );
        return new PageDto<>(patientsDto, metadata);
    }

    public PatientDto getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(PatientNotFoundException::new);
        log.info("Patient with email: {} successfully found", email);
        return mapper.toDto(patient);
    }

    public List<PatientDto> getPatientsByLastName(String lastName) {
        List<PatientDto> patients = patientRepository.findByUserLastNameStartingWithIgnoreCase(lastName).stream()
                .map(mapper::toDto)
                .toList();
        log.info("Found {} patients with lastname: {}", patients.size(), lastName);
        return patients;
    }

    @Transactional
    public PatientDto addPatient(CreatePatientCommand command) {
        validateIfPatientAlreadyExists(command.email());
        Patient patient = new Patient(command);
        User user = userService.getOrCreateUser(command.firstName(), command.lastName());
        patient.addUser(user);
        Patient added = patientRepository.save(patient);
        log.info("Patient with email: {} successfully added", command.email());
        return mapper.toDto(added);
    }

    @Transactional
    public void removePatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(PatientNotFoundException::new);
        User user = patient.getUser();
        patient.removeUser(user);
        patientRepository.delete(patient);
        log.info("Patient with email: {} successfully removed", email);
    }

    @Transactional
    public PatientDto updatePatient(String email, CreatePatientCommand command) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(PatientNotFoundException::new);
        User user = userService.getOrCreateUser(command.firstName(), command.lastName());
        patient.addUser(user);
        patient.updatePatient(command);
        Patient updated = patientRepository.save(patient);
        log.info("Patient with email: {} successfully updated", email);
        return mapper.toDto(updated);
    }

    @Transactional
    public PatientDto changePatientPassword(String email, String password) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(PatientNotFoundException::new);
        patient.setPassword(password);
        patientRepository.save(patient);
        log.info("Password successfully changed for patient with email: {}" ,email);
        return mapper.toDto(patient);
    }

    private void validateIfPatientAlreadyExists(String email) {
        Optional<Patient> foundPatient = patientRepository.findByEmail(email);
        if (foundPatient.isPresent()) {
            throw new ResourceAlreadyExistsException("Patient already exists");
        }
    }
}

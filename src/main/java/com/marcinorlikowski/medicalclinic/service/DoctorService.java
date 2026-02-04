package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.dto.CreateDoctorCommand;
import com.marcinorlikowski.medicalclinic.dto.DoctorDto;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.dto.PageMetadata;
import com.marcinorlikowski.medicalclinic.exceptions.DoctorNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.ResourceAlreadyExistsException;
import com.marcinorlikowski.medicalclinic.mapper.DoctorMapper;
import com.marcinorlikowski.medicalclinic.model.*;
import com.marcinorlikowski.medicalclinic.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorMapper doctorMapper;
    private final DoctorRepository doctorRepository;
    private final UserService userService;

    public PageDto<DoctorDto> getAll(Pageable pageable) {
        Page<Doctor> doctors = doctorRepository.findAll(pageable);
        List<DoctorDto> doctorsDto = doctorMapper.toDto(doctors.getContent());
        PageMetadata metadata = new PageMetadata(
                doctors.getNumber(),
                doctors.getSize(),
                doctors.getTotalElements(),
                doctors.getTotalPages()
        );
        return new PageDto<>(doctorsDto, metadata);
    }

    @Transactional
    public DoctorDto addDoctor(CreateDoctorCommand command) {
        this.validateIfDoctorAlreadyExists(command.email());
        Doctor doctor = new Doctor(command);
        User user = userService.getOrCreateUser(command.firstName(), command.lastName());
        doctor.addUser(user);
        Doctor added = doctorRepository.save(doctor);
        return doctorMapper.toDto(added);
    }

    @Transactional
    public void removeDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(DoctorNotFoundException::new);
        User user = doctor.getUser();
        doctor.removeUser(user);
        doctorRepository.delete(doctor);
    }

    @Transactional
    public DoctorDto updateDoctor(Long doctorId, CreateDoctorCommand command) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(DoctorNotFoundException::new);
        User user = userService.getOrCreateUser(command.firstName(), command.lastName());
        doctor.addUser(user);
        doctor.updateDoctor(command);
        Doctor updated = doctorRepository.save(doctor);
        return doctorMapper.toDto(updated);
    }

    private void validateIfDoctorAlreadyExists(String email) {
        Optional<Doctor> foundDoctor = doctorRepository.findByEmail(email);
        if (foundDoctor.isPresent()) {
            throw new ResourceAlreadyExistsException("ERROR - Doctor already exists");
        }
    }
}

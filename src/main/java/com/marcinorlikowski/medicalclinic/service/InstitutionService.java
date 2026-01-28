package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.dto.PageMetadata;
import com.marcinorlikowski.medicalclinic.exceptions.DoctorNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.InstitutionNotFoundException;
import com.marcinorlikowski.medicalclinic.mapper.InstitutionMapper;
import com.marcinorlikowski.medicalclinic.dto.CreateInstitutionCommand;
import com.marcinorlikowski.medicalclinic.model.Doctor;
import com.marcinorlikowski.medicalclinic.model.Institution;
import com.marcinorlikowski.medicalclinic.dto.InstitutionDto;
import com.marcinorlikowski.medicalclinic.repository.DoctorRepository;
import com.marcinorlikowski.medicalclinic.repository.InstitutionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final InstitutionMapper institutionMapper;
    private final InstitutionRepository institutionRepository;
    private final DoctorRepository doctorRepository;

    public PageDto<InstitutionDto> getAll(Pageable pageable) {
        Page<Institution> institutions = institutionRepository.findAll(pageable);
        List<InstitutionDto> institutionsDto = institutionMapper.toDto(institutions.getContent());
        PageMetadata metadata = new PageMetadata(
                institutions.getNumber(),
                institutions.getSize(),
                institutions.getTotalElements(),
                institutions.getTotalPages()
        );
        return new PageDto<>(institutionsDto, metadata);
    }

    @Transactional
    public InstitutionDto addInstitution(CreateInstitutionCommand command) {
        Institution institution = new Institution(command);
        Institution added = institutionRepository.save(institution);
        return institutionMapper.toDto(added);
    }

    @Transactional
    public void deleteInstitution(Long institutionId) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(InstitutionNotFoundException::new);
        institution.getDoctors()
                .forEach(doctor -> doctor.getInstitutions().remove(institution));
        institutionRepository.delete(institution);
    }

    @Transactional
    public InstitutionDto updateInstitution(Long institutionId, CreateInstitutionCommand command) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(InstitutionNotFoundException::new);
        institution.updateInstitution(command);
        Institution updated = institutionRepository.save(institution);
        return institutionMapper.toDto(updated);
    }

    @Transactional
    public InstitutionDto addDoctorToInstitution(Long institutionId, Long doctorId) {
        Institution institution = institutionRepository.findById(institutionId)
                .orElseThrow(InstitutionNotFoundException::new);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(DoctorNotFoundException::new);
        doctor.assignInstitution(institution);
        doctorRepository.save(doctor);
        Institution saved = institutionRepository.save(institution);
        return institutionMapper.toDto(saved);
    }
}

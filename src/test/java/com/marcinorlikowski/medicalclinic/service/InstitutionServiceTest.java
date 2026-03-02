package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.dto.CreateInstitutionCommand;
import com.marcinorlikowski.medicalclinic.dto.DoctorDto;
import com.marcinorlikowski.medicalclinic.dto.InstitutionDto;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.exceptions.DoctorNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.InstitutionNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.ResourceAlreadyExistsException;
import com.marcinorlikowski.medicalclinic.mapper.InstitutionMapper;
import com.marcinorlikowski.medicalclinic.model.Doctor;
import com.marcinorlikowski.medicalclinic.model.Institution;
import com.marcinorlikowski.medicalclinic.model.Specialization;
import com.marcinorlikowski.medicalclinic.repository.DoctorRepository;
import com.marcinorlikowski.medicalclinic.repository.InstitutionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class InstitutionServiceTest {
    InstitutionMapper institutionMapper;
    InstitutionRepository institutionRepository;
    DoctorRepository doctorRepository;
    InstitutionService institutionService;

    @BeforeEach
    void setup() {
        this.institutionMapper = Mockito.mock(InstitutionMapper.class);
        this.institutionRepository = Mockito.mock(InstitutionRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.institutionService = new InstitutionService(institutionMapper, institutionRepository, doctorRepository);
    }

    @Test
    void getAll_DataCorrect_PageReturned() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        Institution institution1 = new Institution(1L, null, null, null, null,
                null, null);
        Institution institution2 = new Institution(2L, null, null, null, null,
                null, null);
        InstitutionDto dto1 = new InstitutionDto(1L, null, null, null, null,
                null, null);
        InstitutionDto dto2 = new InstitutionDto(2L, null, null, null, null,
                null, null);
        List<Institution> institutions = List.of(institution1, institution2);
        List<InstitutionDto> institutionsDto = List.of(dto1, dto2);
        PageImpl<Institution> institutionsPage = new PageImpl<>(institutions);
        when(institutionRepository.findAll(pageRequest))
                .thenReturn(institutionsPage);
        when(institutionMapper.toDto(institutions))
                .thenReturn(institutionsDto);
        // when
        PageDto<InstitutionDto> result = institutionService.getAll(pageRequest);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1L, result.content().get(0).id()),
                () -> Assertions.assertEquals(2L, result.content().get(1).id())
        );
    }

    @Test
    void addInstitution_DataCorrect_InstitutionAddedAndInstitutionDtoReturned() {
        // given
        CreateInstitutionCommand command = new CreateInstitutionCommand("institution", "city",
                null, null, null);
        Institution institution = new Institution(command);
        InstitutionDto dto = new InstitutionDto(1L, "institution", "city", null,
                null, null, null);
        when(institutionRepository.save(any()))
                .thenReturn(institution);
        when(institutionMapper.toDto(institution))
                .thenReturn(dto);
        // when
        InstitutionDto result = institutionService.addInstitution(command);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1L, result.id()),
                () -> Assertions.assertEquals("institution", result.name()),
                () -> Assertions.assertEquals("city", result.city())
        );
    }

    @Test
    void deleteInstitution_DataCorrect_InstitutionRemoved() {
        // given
        Long institutionId = 1L;
        Institution institution = new Institution();
        Doctor doctor1 = new Doctor();
        Doctor doctor2 = new Doctor();
        institution.setId(institutionId);
        doctor1.getInstitutions().add(institution);
        doctor2.getInstitutions().add(institution);
        institution.getDoctors().add(doctor1);
        institution.getDoctors().add(doctor2);
        when(institutionRepository.findById(institutionId))
                .thenReturn(Optional.of(institution));
        // when
        institutionService.deleteInstitution(institutionId);
        // then
        Assertions.assertAll(
                () -> Assertions.assertTrue(institution.getDoctors().isEmpty()),
                () -> Assertions.assertTrue(doctor1.getInstitutions().isEmpty()),
                () -> Assertions.assertTrue(doctor2.getInstitutions().isEmpty())
        );
        verify(institutionRepository).findById(institutionId);
        verify(institutionRepository).delete(argThat(i -> i.getId().equals(institutionId)));
        verify(institutionRepository).delete(institution);
        verifyNoMoreInteractions(institutionRepository);
    }

    @Test
    void deleteInstitution_InstitutionNotFound_ExceptionThrown() {
        // given
        Long institutionId = 1L;
        when(institutionRepository.findById(institutionId))
                .thenReturn(Optional.empty());
        // when
        InstitutionNotFoundException exception = Assertions.assertThrows(InstitutionNotFoundException.class,
                () -> institutionService.deleteInstitution(institutionId));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Institution not found", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(institutionRepository).findById(institutionId);
        verifyNoMoreInteractions(institutionRepository);
    }

    @Test
    void updateInstitution_DataCorrect_InstitutionUpdatedAndInstitutionDtoReturned() {
        // given
        Long institutionId = 1L;
        CreateInstitutionCommand command = new CreateInstitutionCommand("institution", "city",
                null, null, null);
        Institution institution = new Institution();
        InstitutionDto dto = new InstitutionDto(1L, "institution", "city", null, null,
                null, null);
        when(institutionRepository.findById(institutionId))
                .thenReturn(Optional.of(institution));
        when(institutionRepository.save(any()))
                .thenReturn(institution);
        when(institutionMapper.toDto(institution))
                .thenReturn(dto);
        // when
        InstitutionDto result = institutionService.updateInstitution(institutionId, command);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1L, result.id()),
                () -> Assertions.assertEquals("institution", result.name()),
                () -> Assertions.assertEquals("city", result.city())
        );
    }

    @Test
    void updateInstitution_InstitutionNotFound_ExceptionThrown() {
        // given
        Long institutionId = 1L;
        when(institutionRepository.findById(institutionId))
                .thenReturn(Optional.empty());
        // when
        InstitutionNotFoundException exception = Assertions.assertThrows(InstitutionNotFoundException.class,
                () -> institutionService.deleteInstitution(institutionId));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Institution not found", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(institutionRepository).findById(institutionId);
        verifyNoMoreInteractions(institutionRepository);
    }

    @Test
    void addDoctorToInstitution_DataCorrect_DoctorAddedToInstitutionAndInstitutionDtoReturned() {
        // given
        Long institutionId = 1L;
        Long doctorId = 1L;
        Institution institution = new Institution();
        Doctor doctor = new Doctor();
        DoctorDto doctorDto = new DoctorDto(1L, null, null, null, Specialization.CARDIOLOGIST);
        InstitutionDto institutionDto = new InstitutionDto(1L, null, null, null,
                null, null, List.of(doctorDto));
        when(institutionRepository.findById(any()))
                .thenReturn(Optional.of(institution));
        when(doctorRepository.findById(any()))
                .thenReturn(Optional.of(doctor));
        when(institutionRepository.save(any()))
                .thenReturn(institution);
        when(institutionMapper.toDto(any(Institution.class)))
                .thenReturn(institutionDto);
        // when
        InstitutionDto result = institutionService.addDoctorToInstitution(institutionId, doctorId);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertTrue(institution.getDoctors().contains(doctor)),
                () -> Assertions.assertEquals(List.of(doctorDto), result.doctors())
        );
    }

    @Test
    void addDoctorToInstitution_InstitutionNotFound_ExceptionThrown() {
        // given
        Long institutionId = 1L;
        Long doctorId = 1L;
        when(institutionRepository.findById(institutionId))
                .thenReturn(Optional.empty());
        // when
        InstitutionNotFoundException exception = Assertions.assertThrows(InstitutionNotFoundException.class,
                () -> institutionService.addDoctorToInstitution(institutionId, doctorId));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Institution not found", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(institutionRepository).findById(institutionId);
        verifyNoMoreInteractions(institutionRepository);
    }

    @Test
    void addDoctorToInstitution_DoctorNotFound_ExceptionThrown() {
        // given
        Long institutionId = 1L;
        Long doctorId = 1L;
        Institution institution = new Institution();
        when(institutionRepository.findById(institutionId))
                .thenReturn(Optional.of(institution));
        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> institutionService.addDoctorToInstitution(institutionId, doctorId));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Doctor not found", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(doctorRepository).findById(doctorId);
        verify(institutionRepository).findById(institutionId);
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(institutionRepository);
    }

    @Test
    void addDoctorToInstitution_DoctorAlreadyAssigned_ExceptionThrown() {
        // given
        Long institutionId = 1L;
        Long doctorId = 1L;
        Institution institution = new Institution();
        Doctor doctor = new Doctor();
        institution.getDoctors().add(doctor);
        when(institutionRepository.findById(institutionId))
                .thenReturn(Optional.of(institution));
        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));
        // when
        ResourceAlreadyExistsException exception = Assertions.assertThrows(ResourceAlreadyExistsException.class,
                () -> institutionService.addDoctorToInstitution(institutionId, doctorId));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Doctor is already assigned to this institution", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
        verify(doctorRepository).findById(doctorId);
        verify(institutionRepository).findById(institutionId);
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(institutionRepository);
    }
}

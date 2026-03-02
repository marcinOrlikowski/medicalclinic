package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.dto.CreateDoctorCommand;
import com.marcinorlikowski.medicalclinic.dto.DoctorDto;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.exceptions.DoctorNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.ResourceAlreadyExistsException;
import com.marcinorlikowski.medicalclinic.mapper.DoctorMapper;
import com.marcinorlikowski.medicalclinic.model.Doctor;
import com.marcinorlikowski.medicalclinic.model.Specialization;
import com.marcinorlikowski.medicalclinic.model.User;
import com.marcinorlikowski.medicalclinic.repository.DoctorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {
    DoctorMapper doctorMapper;
    DoctorRepository doctorRepository;
    UserService userService;
    DoctorService doctorService;

    @BeforeEach
    void setup() {
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.userService = Mockito.mock(UserService.class);
        this.doctorService = new DoctorService(doctorMapper, doctorRepository, userService);
    }

    @Test
    void getAll_DataCorrect_PageReturned() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        Doctor doctor1 = new Doctor(1L, "abc@df.com", null, null, null,
                Collections.emptyList(), Collections.emptyList());
        Doctor doctor2 = new Doctor(2L, "abc2@df.com", null, null, null,
                Collections.emptyList(), Collections.emptyList());
        List<Doctor> doctors = List.of(doctor1, doctor2);
        PageImpl<Doctor> doctorsPage = new PageImpl<>(doctors);
        when(doctorRepository.findAll(pageRequest))
                .thenReturn(doctorsPage);
        // when
        PageDto<DoctorDto> result = doctorService.getAll(pageRequest);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1L, result.content().get(0).id()),
                () -> Assertions.assertEquals(2L, result.content().get(1).id()),
                () -> Assertions.assertEquals("abc@df.com", result.content().get(0).email()),
                () -> Assertions.assertEquals("abc2@df.com", result.content().get(1).email())
        );
    }

    @Test
    void addDoctor_DataCorrect_DoctorSavedAndDoctorDtoReturned() {
        // given
        CreateDoctorCommand command = new CreateDoctorCommand("abc@df.com", null, "Sebek",
                "Javowy", "cardiologist");
        User user = new User("Sebek", "Javowy");
        Doctor doctor = new Doctor(command);
        doctor.addUser(user);
        when(userService.getOrCreateUser("Sebek", "Javowy"))
                .thenReturn(user);
        when(doctorRepository.save(any()))
                .thenReturn(doctor);
        // when
        DoctorDto result = doctorService.addDoctor(command);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals("abc@df.com", result.email()),
                () -> Assertions.assertEquals("Sebek", result.firstName()),
                () -> Assertions.assertEquals("Javowy", result.lastName())
        );
    }

    @Test
    void addDoctor_DoctorAlreadyExists_ExceptionThrown() {
        // when
        String email = "abc@df.com";
        CreateDoctorCommand command = new CreateDoctorCommand(email, null, "Sebek",
                "Javowy", "cardiologist");
        Doctor existing = new Doctor();
        existing.setEmail(email);
        when(doctorRepository.findByEmail(email))
                .thenReturn(Optional.of(existing));
        // given
        ResourceAlreadyExistsException exception = Assertions.assertThrows(ResourceAlreadyExistsException.class,
                () -> doctorService.addDoctor(command));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Doctor already exists", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
        verify(doctorRepository).findByEmail(email);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void removeDoctor_DataCorrect_DoctorRemoved() {
        // given
        Long doctorId = 1L;
        Doctor doctor = new Doctor();
        User user = new User();
        doctor.setId(doctorId);
        doctor.setUser(user);
        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));
        // when
        doctorService.removeDoctor(doctorId);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNull(doctor.getUser())
        );
        verify(doctorRepository).findById(doctorId);
//        verify(doctorRepository).delete(argThat(d -> d.getId().equals(doctorId)));
        verify(doctorRepository).delete(doctor);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void removeDoctor_DoctorNotFound_ExceptionThrown() {
        // given
        Long doctorId = 1L;
        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> doctorService.removeDoctor(doctorId));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Doctor not found", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(doctorRepository).findById(doctorId);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void updateDoctor_DataCorrect_DoctorUpdatedAndDoctorDtoReturned() {
        // given
        Long doctorId = 1L;
        CreateDoctorCommand command = new CreateDoctorCommand("abc2@df.com", null, "Sebek",
                "Javowy", "cardiologist");
        User oldUser = new User();
        User newUser = new User("Sebek", "Javowy");
        Doctor doctor = new Doctor();
        doctor.setUser(oldUser);
        when(userService.getOrCreateUser("Sebek", "Javowy"))
                .thenReturn(newUser);
        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any()))
                .thenReturn(doctor);
        // when
        DoctorDto result = doctorService.updateDoctor(doctorId, command);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals("abc2@df.com", result.email()),
                () -> Assertions.assertEquals("Sebek", result.firstName()),
                () -> Assertions.assertEquals("Javowy", result.lastName()),
                () -> Assertions.assertEquals(Specialization.CARDIOLOGIST, result.specialization())
        );
    }

    @Test
    void updateDoctor_DoctorNotFound_ExceptionThrown() {
        // when
        Long doctorId = 1L;
        CreateDoctorCommand command = new CreateDoctorCommand("abc2@df.com", null, "Sebek",
                "Javowy", "cardiologist");
        when(doctorRepository.findById(doctorId))
                .thenReturn(Optional.empty());
        // given
        DoctorNotFoundException exception = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> doctorService.updateDoctor(doctorId, command));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Doctor not found", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(doctorRepository).findById(doctorId);
        verifyNoMoreInteractions(doctorRepository);
    }
}
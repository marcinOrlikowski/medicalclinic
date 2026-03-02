package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinic.dto.CreateAppointmentCommand;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.exceptions.*;
import com.marcinorlikowski.medicalclinic.mapper.AppointmentMapper;
import com.marcinorlikowski.medicalclinic.model.Appointment;
import com.marcinorlikowski.medicalclinic.model.Doctor;
import com.marcinorlikowski.medicalclinic.model.Patient;
import com.marcinorlikowski.medicalclinic.model.Period;
import com.marcinorlikowski.medicalclinic.repository.AppointmentsRepository;
import com.marcinorlikowski.medicalclinic.repository.DoctorRepository;
import com.marcinorlikowski.medicalclinic.repository.PatientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AppointmentServiceTest {
    AppointmentMapper appointmentMapper;
    AppointmentsRepository appointmentsRepository;
    DoctorRepository doctorRepository;
    PatientRepository patientRepository;
    AppointmentService appointmentService;

    @BeforeEach
    void setup() {
        this.appointmentMapper = Mappers.getMapper(AppointmentMapper.class);
        this.appointmentsRepository = Mockito.mock(AppointmentsRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.appointmentService = new AppointmentService(appointmentsRepository, doctorRepository,
                patientRepository, appointmentMapper);
    }

    @Test
    void getAll_DataCorrect_PageReturned() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 20);
        Appointment appointment1 = new Appointment();
        appointment1.setId(1L);
        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        List<Appointment> appointments = List.of(appointment1, appointment2);
        PageImpl<Appointment> appointmentsPage = new PageImpl<>(appointments);
        when(appointmentsRepository.findAll(pageRequest))
                .thenReturn(appointmentsPage);
        // when
        PageDto<AppointmentDto> result = appointmentService.getAll(pageRequest);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(2, result.content().size()),
                () -> Assertions.assertEquals(1L, result.content().get(0).id()),
                () -> Assertions.assertEquals(2L, result.content().get(1).id())
        );

    }

    @Test
    void getAllByDoctorId_DataCorrect_PageReturned() {
        // given
        Long doctorId = 1L;
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        Appointment appointment1 = new Appointment();
        appointment1.setDoctor(doctor);
        Appointment appointment2 = new Appointment();
        appointment2.setDoctor(doctor);
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<Appointment> appointments = List.of(appointment1, appointment2);
        PageImpl<Appointment> appointmentsPage = new PageImpl<>(appointments);
        when(appointmentsRepository.findAllByDoctorId(pageRequest, doctorId))
                .thenReturn(appointmentsPage);
        // when
        PageDto<AppointmentDto> result = appointmentService.getAllByDoctorId(pageRequest, doctorId);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(2, result.content().size()),
                () -> Assertions.assertEquals(1L, result.content().get(0).doctorId()),
                () -> Assertions.assertEquals(1L, result.content().get(1).doctorId())
        );
    }

    @Test
    void getAllByPatientId_DataCorrect_PageReturned() {
        // given
        Long patientId = 1L;
        Patient patient = new Patient();
        patient.setId(patientId);
        Appointment appointment1 = new Appointment();
        appointment1.setPatient(patient);
        Appointment appointment2 = new Appointment();
        appointment2.setPatient(patient);
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<Appointment> appointments = List.of(appointment1, appointment2);
        PageImpl<Appointment> appointmentsPage = new PageImpl<>(appointments);
        when(appointmentsRepository.findAllByPatientId(pageRequest, patientId))
                .thenReturn(appointmentsPage);
        // when
        PageDto<AppointmentDto> result = appointmentService.getAllByPatientId(pageRequest, patientId);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(2, result.content().size()),
                () -> Assertions.assertEquals(1L, result.content().get(0).patientId()),
                () -> Assertions.assertEquals(1L, result.content().get(1).patientId())
        );
    }

    @Test
    void createAppointment_DataCorrect_AppointmentCreatedAndAppointmentDtoReturned() {
        // given
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                LocalDateTime.of(2027, 2, 15, 15, 0),
                LocalDateTime.of(2027, 2, 15, 16, 0),
                1L);
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        Appointment appointment = new Appointment(command);
        doctor.addAppointment(appointment);
        when(doctorRepository.findById(any()))
                .thenReturn(Optional.of(doctor));
        when(appointmentsRepository.save(any()))
                .thenReturn(appointment);
        // when
        AppointmentDto result = appointmentService.createAppointment(command);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertTrue(doctor.getAppointments().contains(appointment)),
                () -> Assertions.assertEquals(1L, result.doctorId()),
                () -> Assertions.assertEquals(1L, appointment.getDoctor().getId())
        );
    }

    @Test
    void createAppointment_TimeValidationFailed_ExceptionThrown() {
        // given
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2026, 2, 15, 16, 0),
                1L);
        // when
        InvalidDateTimeException exception = Assertions.assertThrows(InvalidDateTimeException.class,
                () -> appointmentService.createAppointment(command));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("End date must be after start date", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus())
        );
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(appointmentsRepository);
    }

    @Test
    void createAppointment_DoctorNotFound_ExceptionThrown() {
        // given
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0),
                1L);
        when(doctorRepository.findById(any()))
                .thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = Assertions.assertThrows(DoctorNotFoundException.class,
                () -> appointmentService.createAppointment(command));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Doctor not found", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(doctorRepository).findById(command.doctorId());
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(appointmentsRepository);
    }

    @Test
    void createAppointment_AppointmentOverlaps_ExceptionThrown() {
        // given
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0),
                1L);
        Doctor doctor = new Doctor();
        when(doctorRepository.findById(any()))
                .thenReturn(Optional.of(doctor));
        when(appointmentsRepository.existsOverlappingAppointment(any(), any(), any()))
                .thenReturn(true);
        // when
        AppointmentNotAvailableException exception = Assertions.assertThrows(AppointmentNotAvailableException.class,
                () -> appointmentService.createAppointment(command));
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Appointment overlaps", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
        verify(doctorRepository).findById(command.doctorId());
        verify(appointmentsRepository).existsOverlappingAppointment(any(), any(), any());
        verifyNoMoreInteractions(doctorRepository);
        verifyNoMoreInteractions(appointmentsRepository);
    }

    @Test
    void assignPatient_DataCorrect_PatientAssignedAndAppointmentDtoReturned() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        Period period = Period.of(LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0));
        Appointment appointment = new Appointment(appointmentId, period, null, null);
        Patient patient = new Patient();
        patient.setId(patientId);
        when(appointmentsRepository.findById(appointmentId))
                .thenReturn(Optional.of(appointment));
        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));
        when(appointmentsRepository.save(any()))
                .thenReturn(appointment);
        // when
        AppointmentDto result = appointmentService.assignPatient(appointmentId, patientId);
        //then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(1L, result.patientId()),
                () -> Assertions.assertTrue(patient.getAppointments().contains(appointment))
        );
    }

    @Test
    void assignPatient_AppointmentNotFound_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        when(appointmentsRepository.findById(any()))
                .thenReturn(Optional.empty());
        // when
        AppointmentNotFoundException exception = Assertions.assertThrows(AppointmentNotFoundException.class,
                () -> appointmentService.assignPatient(appointmentId, patientId));
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Appointment not found", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(appointmentsRepository).findById(any());
        verifyNoMoreInteractions(appointmentsRepository);
    }

    @Test
    void assignPatient_AppointmentNotAvailable_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        Appointment appointment = new Appointment();
        when(appointmentsRepository.findById(any()))
                .thenReturn(Optional.of(appointment));
        Patient patient = new Patient();
        appointment.setPatient(patient);
        // when
        PatientAlreadyAssignedException exception = Assertions.assertThrows(PatientAlreadyAssignedException.class,
                () -> appointmentService.assignPatient(appointmentId, patientId));
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals("This appointment is already taken", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
        verify(appointmentsRepository).findById(any());
        verifyNoMoreInteractions(appointmentsRepository);
    }

    @Test
    void assignPatient_PatientNotFound_ExceptionThrown() {
        // given
        Long appointmentId = 1L;
        Long patientId = 1L;
        Period period = Period.of(LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0));
        Appointment appointment = new Appointment(appointmentId, period, null, null);
        when(appointmentsRepository.findById(any()))
                .thenReturn(Optional.of(appointment));
        when(patientRepository.findById(any()))
                .thenReturn(Optional.empty());
        // when
        PatientNotFoundException exception = Assertions.assertThrows(PatientNotFoundException.class,
                () -> appointmentService.assignPatient(appointmentId, patientId));
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals("Patient not found", exception.getMessage()),
                () -> Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
        verify(appointmentsRepository).findById(any());
        verify(patientRepository).findById(any());
        verifyNoMoreInteractions(appointmentsRepository);
        verifyNoMoreInteractions(patientRepository);
    }
}

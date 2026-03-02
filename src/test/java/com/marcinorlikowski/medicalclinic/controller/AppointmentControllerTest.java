package com.marcinorlikowski.medicalclinic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinorlikowski.medicalclinic.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinic.dto.CreateAppointmentCommand;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.dto.PageMetadata;
import com.marcinorlikowski.medicalclinic.exceptions.*;
import com.marcinorlikowski.medicalclinic.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_DataCorrect_PageReturned() throws Exception {
        AppointmentDto appointmentDto1 = new AppointmentDto(
                1L, LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0), 1L, 1L);
        AppointmentDto appointmentDto2 = new AppointmentDto(
                2L, LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0), 1L, 1L);
        List<AppointmentDto> appointmentsDto = List.of(appointmentDto1, appointmentDto2);
        PageImpl<AppointmentDto> page = new PageImpl<>(appointmentsDto);
        PageMetadata metadata = new PageMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        PageDto<AppointmentDto> pageDto = new PageDto<>(appointmentsDto, metadata);

        when(appointmentService.getAll(any()))
                .thenReturn(pageDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(appointmentDto1.id()))
                .andExpect(jsonPath("$.content[1].id").value(appointmentDto2.id()));
    }

    @Test
    void getAllByDoctorId_DataCorrect_PageReturned() throws Exception {
        Long doctorId = 1L;
        AppointmentDto appointmentDto = new AppointmentDto(
                1L, LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0), doctorId, 1L);
        List<AppointmentDto> appointmentsDto = List.of(appointmentDto);
        PageImpl<AppointmentDto> page = new PageImpl<>(appointmentsDto);
        PageMetadata metadata = new PageMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        PageDto<AppointmentDto> pageDto = new PageDto<>(appointmentsDto, metadata);

        when(appointmentService.getAllByDoctorId(any(), eq(doctorId)))
                .thenReturn(pageDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments/doctors/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(appointmentDto.id()))
                .andExpect(jsonPath("$.content[0].doctorId").value(doctorId));
    }

    @Test
    void getAllByPatientId_DataCorrect_PageReturned() throws Exception {
        Long patientId = 1L;
        AppointmentDto appointmentDto = new AppointmentDto(
                1L, LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0), 1L, 1L);
        List<AppointmentDto> appointmentsDto = List.of(appointmentDto);
        PageImpl<AppointmentDto> page = new PageImpl<>(appointmentsDto);
        PageMetadata metadata = new PageMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        PageDto<AppointmentDto> pageDto = new PageDto<>(appointmentsDto, metadata);

        when(appointmentService.getAllByPatientId(any(), eq(patientId)))
                .thenReturn(pageDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments/patients/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(appointmentDto.id()))
                .andExpect(jsonPath("$.content[0].patientId").value(patientId));
    }

    @Test
    void createAppointment_DataCorrect_AppointmentCreated() throws Exception {
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0),
                1L);
        AppointmentDto appointmentDto = new AppointmentDto(
                1L, LocalDateTime.of(2050, 2, 15, 15, 0, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0, 0), 1L, null);

        when(appointmentService.createAppointment(command))
                .thenReturn(appointmentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/appointments")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(appointmentDto.id()))
                .andExpect(jsonPath("$.startDate").value("2050-02-15T15:00:00"))
                .andExpect(jsonPath("$.endDate").value("2050-02-15T16:00:00"))
                .andExpect(jsonPath("$.doctorId").value(appointmentDto.doctorId()));
        verify(appointmentService).createAppointment(command);
        verifyNoMoreInteractions(appointmentService);
    }

    @Test
    void createAppointment_ValidationFailed_ExceptionThrown() throws Exception {
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0),
                null);

        mockMvc.perform(MockMvcRequestBuilders.post("/appointments")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message")
                                .value("One or more fields have validation errors"));
        verifyNoInteractions(appointmentService);
    }

    @Test
    void createAppointment_AppointmentOverlaps_ExceptionThrown() throws Exception {
        CreateAppointmentCommand command = new CreateAppointmentCommand(
                LocalDateTime.of(2050, 2, 15, 15, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0),
                1L);
        when(appointmentService.createAppointment(command))
                .thenThrow(new AppointmentNotAvailableException());

        mockMvc.perform(MockMvcRequestBuilders.post("/appointments")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("Appointment overlaps"));
        verify(appointmentService).createAppointment(command);
        verifyNoMoreInteractions(appointmentService);
    }

    @Test
    void assignPatient_DataCorrect_PatientAssignedToAppointment() throws Exception {
        Long appointmentId = 1L;
        Long patientId = 1L;
        AppointmentDto appointmentDto = new AppointmentDto(
                appointmentId, LocalDateTime.of(2050, 2, 15, 15, 0, 0),
                LocalDateTime.of(2050, 2, 15, 16, 0, 0),
                1L, patientId);

        when(appointmentService.assignPatient(appointmentId, patientId))
                .thenReturn(appointmentDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/appointments/1/patient/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(patientId));
        verify(appointmentService).assignPatient(appointmentId, patientId);
        verifyNoMoreInteractions(appointmentService);
    }

    @Test
    void assignPatient_AppointmentNotFound_ExceptionThrown() throws Exception {
        when(appointmentService.assignPatient(any(), any()))
                .thenThrow(new AppointmentNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/appointments/1/patient/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Appointment not found"));
    }

    @Test
    void assignPatient_PatientNotFound_ExceptionThrown() throws Exception {
        when(appointmentService.assignPatient(any(), any()))
                .thenThrow(new PatientNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/appointments/1/patient/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    void assignPatient_AppointmentAlreadyTaken_ExceptionThrown() throws Exception {
        when(appointmentService.assignPatient(any(), any()))
                .thenThrow(new PatientAlreadyAssignedException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/appointments/1/patient/1"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This appointment is already taken"));
    }

}

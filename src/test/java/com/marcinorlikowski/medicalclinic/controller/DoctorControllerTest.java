package com.marcinorlikowski.medicalclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinorlikowski.medicalclinic.dto.*;
import com.marcinorlikowski.medicalclinic.exceptions.DoctorNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.ResourceAlreadyExistsException;
import com.marcinorlikowski.medicalclinic.model.Specialization;
import com.marcinorlikowski.medicalclinic.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DoctorService doctorService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_DataCorrect_PageReturned() throws Exception {
        DoctorDto doctorDto1 = new DoctorDto(1L, "abc@df.com", "Sebek",
                "Javowy", Specialization.CARDIOLOGIST);
        DoctorDto doctorDto2 = new DoctorDto(2L, "abc@df.com", "Drugi",
                "Sebek", Specialization.SURGEON);
        List<DoctorDto> doctorsDto = List.of(doctorDto1, doctorDto2);
        PageImpl<DoctorDto> page = new PageImpl<>(doctorsDto);
        PageMetadata metadata = new PageMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        PageDto<DoctorDto> pageDto = new PageDto<>(doctorsDto, metadata);

        when(doctorService.getAll(any()))
                .thenReturn(pageDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/doctors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].firstName").value(doctorDto1.firstName()))
                .andExpect(jsonPath("$.content[1].firstName").value(doctorDto2.firstName()));
    }

    @Test
    void addDoctor_DataCorrect_DoctorAdded() throws Exception {
        CreateDoctorCommand command = new CreateDoctorCommand("abc@df.com", "pass", "Sebek",
                "Javowy", "CARDIOLOGIST");
        DoctorDto doctorDto = new DoctorDto(1L, "abc@df.com", "Sebek",
                "Javowy", Specialization.CARDIOLOGIST);
        when(doctorService.addDoctor(command))
                .thenReturn(doctorDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(doctorDto.id()))
                .andExpect(jsonPath("$.email").value(doctorDto.email()))
                .andExpect(jsonPath("$.firstName").value(doctorDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(doctorDto.lastName()))
                .andExpect(jsonPath("$.specialization").value(doctorDto.specialization().name()));
        verify(doctorService).addDoctor(command);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void addDoctor_ValidationFailed_ExceptionThrown() throws Exception {
        CreateDoctorCommand command = new CreateDoctorCommand("abc@df.com", null, "Sebek",
                "Javowy", "CARDIOLOGIST");
        mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("One or more fields have validation errors"));
        verifyNoInteractions(doctorService);
    }

    @Test
    void addDoctor_DoctorAlreadyExists_ExceptionThrown() throws Exception {
        CreateDoctorCommand command = new CreateDoctorCommand("abc@df.com", "password", "Sebek",
                "Javowy", "CARDIOLOGIST");
        when(doctorService.addDoctor(command))
                .thenThrow(new ResourceAlreadyExistsException("Doctor already exists"));

        mockMvc.perform(MockMvcRequestBuilders.post("/doctors")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Doctor already exists"));
        verify(doctorService).addDoctor(command);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void removeDoctor_DataCorrect_DoctorRemoved() throws Exception {
        Long doctorId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(doctorService).removeDoctor(doctorId);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void removeDoctor_DoctorNotFound_DoctorRemoved() throws Exception {
        Long doctorId = 1L;
        doThrow(new DoctorNotFoundException())
                .when(doctorService).removeDoctor(doctorId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/doctors/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"));
        verify(doctorService).removeDoctor(doctorId);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void updateDoctor_DataCorrect_DoctorUpdated() throws Exception {
        Long doctorId = 1L;
        CreateDoctorCommand command = new CreateDoctorCommand("abc@df.com", "pass", "Nowy",
                "Sebek", "CARDIOLOGIST");
        DoctorDto updatedDto = new DoctorDto(doctorId, "abc@df.com", "Nowy",
                "Sebek", Specialization.CARDIOLOGIST);
        when(doctorService.updateDoctor(doctorId, command))
                .thenReturn(updatedDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/1")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedDto.id()))
                .andExpect(jsonPath("$.email").value(updatedDto.email()))
                .andExpect(jsonPath("$.firstName").value(updatedDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(updatedDto.lastName()))
                .andExpect(jsonPath("$.specialization").value(updatedDto.specialization().name()));
        verify(doctorService).updateDoctor(doctorId, command);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    void updateDoctor_ValidationFailed_ExceptionThrown() throws Exception {
        CreateDoctorCommand command = new CreateDoctorCommand("abc@df.com", null, "Nowy",
                "Sebek", "CARDIOLOGIST");
        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/1")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("One or more fields have validation errors"));
        verifyNoInteractions(doctorService);
    }

    @Test
    void updateDoctor_DoctorNotFound_ExceptionThrown() throws Exception {
        Long doctorId = 1L;
        CreateDoctorCommand command = new CreateDoctorCommand("abc@df.com", "password", "Nowy",
                "Sebek", "CARDIOLOGIST");
        when(doctorService.updateDoctor(doctorId, command))
                .thenThrow(new DoctorNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/doctors/1")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"));
        verify(doctorService).updateDoctor(doctorId, command);
        verifyNoMoreInteractions(doctorService);
    }

}

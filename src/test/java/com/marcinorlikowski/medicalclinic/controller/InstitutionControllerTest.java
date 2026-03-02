package com.marcinorlikowski.medicalclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinorlikowski.medicalclinic.dto.*;
import com.marcinorlikowski.medicalclinic.exceptions.DoctorNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.InstitutionNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.ResourceAlreadyExistsException;
import com.marcinorlikowski.medicalclinic.model.Specialization;
import com.marcinorlikowski.medicalclinic.service.InstitutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InstitutionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private InstitutionService institutionService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_DataCorrect_PageReturned() throws Exception {
        InstitutionDto institutionDto1 = new InstitutionDto(1L, "institution1", "city1", "12345", "street",
                "11A", Collections.emptyList());
        InstitutionDto institutionDto2 = new InstitutionDto(1L, "institution2", "city2", "12345", "street",
                "22A", Collections.emptyList());
        List<InstitutionDto> institutionsDto = List.of(institutionDto1, institutionDto2);
        PageImpl<InstitutionDto> page = new PageImpl<>(institutionsDto);
        PageMetadata metadata = new PageMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        PageDto<InstitutionDto> pageDto = new PageDto<>(institutionsDto, metadata);

        when(institutionService.getAll(any()))
                .thenReturn(pageDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/institutions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value(institutionDto1.name()))
                .andExpect(jsonPath("$.content[1].name").value(institutionDto2.name()));
    }

    @Test
    void addInstitution_DataCorrect_DoctorAdded() throws Exception {
        CreateInstitutionCommand command = new CreateInstitutionCommand("institution", "city",
                "12345", "street", "11A");
        InstitutionDto institutionDto = new InstitutionDto(1L, "institution", "city", "12345", "street",
                "11A", Collections.emptyList());
        when(institutionService.addInstitution(command))
                .thenReturn(institutionDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/institutions")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(institutionDto.id()))
                .andExpect(jsonPath("$.name").value(institutionDto.name()))
                .andExpect(jsonPath("$.city").value(institutionDto.city()))
                .andExpect(jsonPath("$.postalCode").value(institutionDto.postalCode()))
                .andExpect(jsonPath("$.buildingNumber").value(institutionDto.buildingNumber()))
                .andExpect(jsonPath("$.doctors").isEmpty());
        verify(institutionService).addInstitution(command);
        verifyNoMoreInteractions(institutionService);
    }

    @Test
    void addInstitution_ValidationFailed_ExceptionThrown() throws Exception {
        CreateInstitutionCommand command = new CreateInstitutionCommand(null, "city",
                "12345", "street", "11A");
        mockMvc.perform(MockMvcRequestBuilders.post("/institutions")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("One or more fields have validation errors"));
        verifyNoInteractions(institutionService);
    }

    @Test
    void addInstitution_InstitutionAlreadyExists_ExceptionThrown() throws Exception {
        CreateInstitutionCommand command = new CreateInstitutionCommand("institution", "city",
                "12345", "street", "11A");
        when(institutionService.addInstitution(command))
                .thenThrow(new ResourceAlreadyExistsException("Institution already exists"));

        mockMvc.perform(MockMvcRequestBuilders.post("/institutions")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Institution already exists"));
        verify(institutionService).addInstitution(command);
        verifyNoMoreInteractions(institutionService);
    }

    @Test
    void removeInstitution_DataCorrect_InstitutionRemoved() throws Exception {
        Long institutionId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/institutions/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(institutionService).deleteInstitution(institutionId);
        verifyNoMoreInteractions(institutionService);
    }

    @Test
    void removeInstitution_InstitutionNotFound_DoctorRemoved() throws Exception {
        Long institutionId = 1L;
        doThrow(new InstitutionNotFoundException())
                .when(institutionService).deleteInstitution(institutionId);
        mockMvc.perform(MockMvcRequestBuilders.delete("/institutions/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Institution not found"));
        verify(institutionService).deleteInstitution(institutionId);
        verifyNoMoreInteractions(institutionService);
    }

    @Test
    void updateInstitution_DataCorrect_InstitutionUpdated() throws Exception {
        Long institutionId = 1L;
        CreateInstitutionCommand command = new CreateInstitutionCommand("institution", "city",
                "12345", "street", "11A");
        InstitutionDto updatedDto = new InstitutionDto(institutionId, "institution", "city", "12345", "street",
                "11A", Collections.emptyList());
        when(institutionService.updateInstitution(institutionId, command))
                .thenReturn(updatedDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/institutions/1")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedDto.id()))
                .andExpect(jsonPath("$.name").value(updatedDto.name()))
                .andExpect(jsonPath("$.city").value(updatedDto.city()))
                .andExpect(jsonPath("$.postalCode").value(updatedDto.postalCode()))
                .andExpect(jsonPath("$.buildingNumber").value(updatedDto.buildingNumber()))
                .andExpect(jsonPath("$.doctors").isEmpty());;
        verify(institutionService).updateInstitution(institutionId, command);
        verifyNoMoreInteractions(institutionService);
    }

    @Test
    void updateInstitution_ValidationFailed_ExceptionThrown() throws Exception {
        CreateInstitutionCommand command = new CreateInstitutionCommand(null, "city",
                "12345", "street", "11A");
        mockMvc.perform(MockMvcRequestBuilders.put("/institutions/1")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("One or more fields have validation errors"));
        verifyNoInteractions(institutionService);
    }

    @Test
    void updateInstitution_InstitutionNotFound_ExceptionThrown() throws Exception {
        Long institutionId = 1L;
        CreateInstitutionCommand command = new CreateInstitutionCommand("institution", "city",
                "12345", "street", "11A");
        when(institutionService.updateInstitution(institutionId, command))
                .thenThrow(new InstitutionNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/institutions/1")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Institution not found"));
        verify(institutionService).updateInstitution(institutionId, command);
        verifyNoMoreInteractions(institutionService);
    }

    @Test
    void addDoctorToInstitution_DataCorrect_DoctorAddedToInstitution() throws Exception {
        Long institutionId = 1L;
        Long doctorId = 1L;
        DoctorDto doctorDto = new DoctorDto(1L, "abc@df.com", "Sebek",
                "Javowy", Specialization.CARDIOLOGIST);
        InstitutionDto institutionDto = new InstitutionDto(institutionId, "institution", "city", "12345", "street",
                "11A", List.of(doctorDto));
        when(institutionService.addDoctorToInstitution(institutionId, doctorId))
                .thenReturn(institutionDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/institutions/1/doctors/1"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.doctors").isNotEmpty())
                .andExpect(jsonPath("$.doctors[0].id").value(doctorId));
        verify(institutionService).addDoctorToInstitution(institutionId, doctorId);
        verifyNoMoreInteractions(institutionService);
    }

    @Test
    void addDoctorToInstitution_InstitutionNotFound_ExceptionThrown() throws Exception {
        Long institutionId = 1L;
        Long doctorId = 1L;
        when(institutionService.addDoctorToInstitution(institutionId, doctorId))
                .thenThrow(new InstitutionNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/institutions/1/doctors/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Institution not found"));
        verify(institutionService).addDoctorToInstitution(institutionId, doctorId);
        verifyNoMoreInteractions(institutionService);
    }

    @Test
    void addDoctorToInstitution_DoctorNotFound_ExceptionThrown() throws Exception {
        Long institutionId = 1L;
        Long doctorId = 1L;
        when(institutionService.addDoctorToInstitution(institutionId, doctorId))
                .thenThrow(new DoctorNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.post("/institutions/1/doctors/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"));
        verify(institutionService).addDoctorToInstitution(institutionId, doctorId);
        verifyNoMoreInteractions(institutionService);
    }

    @Test
    void addDoctorToInstitution_DoctorAlreadyAssigned_ExceptionThrown() throws Exception {
        Long institutionId = 1L;
        Long doctorId = 1L;
        when(institutionService.addDoctorToInstitution(institutionId, doctorId))
                .thenThrow(new ResourceAlreadyExistsException("Doctor is already assigned to this institution"));

        mockMvc.perform(MockMvcRequestBuilders.post("/institutions/1/doctors/1"))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Doctor is already assigned to this institution"));
        verify(institutionService).addDoctorToInstitution(institutionId, doctorId);
        verifyNoMoreInteractions(institutionService);
    }
 }

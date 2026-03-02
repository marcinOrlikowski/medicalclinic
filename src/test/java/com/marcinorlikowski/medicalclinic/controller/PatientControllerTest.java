package com.marcinorlikowski.medicalclinic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinorlikowski.medicalclinic.dto.*;
import com.marcinorlikowski.medicalclinic.exceptions.PatientNotFoundException;
import com.marcinorlikowski.medicalclinic.exceptions.ResourceAlreadyExistsException;
import com.marcinorlikowski.medicalclinic.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PatientService patientService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_DataCorrect_PageReturned() throws Exception {
        PatientDto patientDto1 = new PatientDto(1L, "abc@df.com", "123", "Sebek",
                "Javowy", "123456789", LocalDate.of(1999, 11, 6));
        PatientDto patientDto2 = new PatientDto(2L, "abc2@df.com", "123", "Sebek",
                "Javowy", "123456789", LocalDate.of(1999, 11, 6));
        List<PatientDto> patientsDto = List.of(patientDto1, patientDto2);
        PageImpl<PatientDto> patientPage = new PageImpl<>(patientsDto);

        PageMetadata metadata = new PageMetadata(
                patientPage.getNumber(),
                patientPage.getSize(),
                patientPage.getTotalElements(),
                patientPage.getTotalPages()
        );
        PageDto<PatientDto> pageDto = new PageDto<>(patientsDto, metadata);

        when(patientService.getAll(any()))
                .thenReturn(pageDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/patients"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].email").value("abc@df.com"))
                .andExpect(jsonPath("$.content[1].email").value("abc2@df.com"));
    }

    @Test
    void getPatientByEmail_DataCorrect_PatientDtoReturned() throws Exception {
        String email = "abc@df.com";
        PatientDto patientDto = new PatientDto(1L, "abc@df.com", "123", "Sebek", "Javowy",
                "123456789", LocalDate.of(1999, 11, 6));
        when(patientService.getPatientByEmail(email))
                .thenReturn(patientDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/email/abc@df.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientDto.id()))
                .andExpect(jsonPath("$.email").value(patientDto.email()))
                .andExpect(jsonPath("$.idCardNo").value(patientDto.idCardNo()))
                .andExpect(jsonPath("$.firstName").value(patientDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(patientDto.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(patientDto.phoneNumber()))
                .andExpect(jsonPath("$.birthDate").value(patientDto.birthDate().toString()));
        verify(patientService).getPatientByEmail(email);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void getPatientByEmail_PatientNotFound_ExceptionThrown() throws Exception {
        String email = "abc@df.com";
        when(patientService.getPatientByEmail(email))
                .thenThrow(new PatientNotFoundException());
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/email/abc@df.com"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"));
        verify(patientService).getPatientByEmail(email);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void getPatientsByLastName_DataCorrect_ListOfPatientsDtoReturned() throws Exception {
        String lastName = "Javowy";
        PatientDto patientDto = new PatientDto(1L, "abc@df.com", "123", "Sebek", "Javowy",
                "123456789", LocalDate.of(1999, 11, 6));
        when(patientService.getPatientsByLastName(lastName))
                .thenReturn(List.of(patientDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/patients/lastname/Javowy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(patientDto.id()))
                .andExpect(jsonPath("$[0].email").value(patientDto.email()))
                .andExpect(jsonPath("$[0].idCardNo").value(patientDto.idCardNo()))
                .andExpect(jsonPath("$[0].firstName").value(patientDto.firstName()))
                .andExpect(jsonPath("$[0].lastName").value(patientDto.lastName()))
                .andExpect(jsonPath("$[0].phoneNumber").value(patientDto.phoneNumber()))
                .andExpect(jsonPath("$[0].birthDate").value(patientDto.birthDate().toString()));
        verify(patientService).getPatientsByLastName(lastName);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void addPatient_DataCorrect_PatientAdded() throws Exception {
        CreatePatientCommand command = new CreatePatientCommand(
                "abc@df.com", "pass", "123", "Sebek", "Javowy",
                "123456789", LocalDate.of(1999, 11, 6)
        );
        PatientDto patientDto = new PatientDto(1L, "abc@df.com", "123", "Sebek", "Javowy",
                "123456789", LocalDate.of(1999, 11, 6));

        when(patientService.addPatient(any()))
                .thenReturn(patientDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/patients")
                                .content(objectMapper.writeValueAsString(command))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(patientDto.id()))
                .andExpect(jsonPath("$.email").value(patientDto.email()))
                .andExpect(jsonPath("$.idCardNo").value(patientDto.idCardNo()))
                .andExpect(jsonPath("$.firstName").value(patientDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(patientDto.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(patientDto.phoneNumber()))
                .andExpect(jsonPath("$.birthDate").value(patientDto.birthDate().toString()));
        verify(patientService).addPatient(command);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void addPatient_ValidationFailed_ExceptionThrown() throws Exception {
        CreatePatientCommand command = new CreatePatientCommand(
                "abc@df.com", null, "123", "Sebek", "Javowy",
                "123456789", LocalDate.of(1999, 11, 6)
        );
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/patients")
                                .content(objectMapper.writeValueAsString(command))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("One or more fields have validation errors"));
        verifyNoInteractions(patientService);
    }

    @Test
    void addPatient_PatientAlreadyExists_ExceptionThrown() throws Exception {
        CreatePatientCommand command = new CreatePatientCommand(
                "abc@df.com", "pass", "123", "Sebek", "Javowy",
                "123456789", LocalDate.of(1999, 11, 6)
        );
        when(patientService.addPatient(any()))
                .thenThrow(new ResourceAlreadyExistsException("Patient already exists"));
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/patients")
                                .content(objectMapper.writeValueAsString(command))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Patient already exists"));
        verify(patientService).addPatient(any());
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void removePatient_DataCorrect_PatientRemoved() throws Exception {
        String email = "abc@df.com";
        mockMvc.perform(MockMvcRequestBuilders.delete("/patients/abc@df.com"))
                .andDo(print())
                .andExpect(status().isNoContent());
        verify(patientService).removePatientByEmail(email);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void removePatient_PatientNotFound_ExceptionThrown() throws Exception {
        String email = "abc@df.com";
        doThrow(new PatientNotFoundException())
                .when(patientService).removePatientByEmail(email);
        mockMvc.perform(MockMvcRequestBuilders.delete("/patients/abc@df.com"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"));
        verify(patientService).removePatientByEmail(email);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void updatePatient_DataCorrect_PatientUpdated() throws Exception {
        String email = "abc@df.com";
        CreatePatientCommand command = new CreatePatientCommand(
                email, "pass", "123", "Nowy", "Sebek",
                "123456789", LocalDate.of(1999, 11, 6)
        );
        PatientDto updatedDto = new PatientDto(1L, email, "123", "Nowy",
                "Sebek", "123456789", LocalDate.of(1999, 11, 6));
        when(patientService.updatePatient(email, command))
                .thenReturn(updatedDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/patients/abc@df.com")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedDto.id()))
                .andExpect(jsonPath("$.email").value(updatedDto.email()))
                .andExpect(jsonPath("$.idCardNo").value(updatedDto.idCardNo()))
                .andExpect(jsonPath("$.firstName").value(updatedDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(updatedDto.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(updatedDto.phoneNumber()))
                .andExpect(jsonPath("$.birthDate").value(updatedDto.birthDate().toString()));
        verify(patientService).updatePatient(email, command);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void updatePatient_ValidationFailed_ExceptionThrown() throws Exception {
        String email = "abc@df.com";
        CreatePatientCommand command = new CreatePatientCommand(
                email, null, "123", "Nowy", "Sebek",
                "123456789", LocalDate.of(1999, 11, 6)
        );
        mockMvc.perform(MockMvcRequestBuilders.put("/patients/abc@df.com")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("One or more fields have validation errors"));
        verifyNoInteractions(patientService);
    }

    @Test
    void updatePatient_PatientNotFound_ExceptionThrown() throws Exception {
        String email = "abc@df.com";
        CreatePatientCommand command = new CreatePatientCommand(
                email, "pass", "123", "Nowy", "Sebek",
                "123456789", LocalDate.of(1999, 11, 6)
        );
        when(patientService.updatePatient(email, command))
                .thenThrow(new PatientNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.put("/patients/abc@df.com")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"));
        verify(patientService).updatePatient(email, command);
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void changePassword_DataCorrect_PasswordChanged() throws Exception {
        String email = "abc@df.com";
        PatientDto patientDto = new PatientDto(1L, "abc@df.com", "123", "Sebek",
                "Javowy", "123456789", LocalDate.of(1999, 11, 6));
        ChangePasswordCommand command = new ChangePasswordCommand("newPassword");

        when(patientService.changePatientPassword(email, command.password()))
                .thenReturn(patientDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/patients/abc@df.com")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientDto.id()))
                .andExpect(jsonPath("$.email").value(patientDto.email()))
                .andExpect(jsonPath("$.idCardNo").value(patientDto.idCardNo()))
                .andExpect(jsonPath("$.firstName").value(patientDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(patientDto.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(patientDto.phoneNumber()))
                .andExpect(jsonPath("$.birthDate").value(patientDto.birthDate().toString()));
        verify(patientService).changePatientPassword(email, command.password());
        verifyNoMoreInteractions(patientService);
    }

    @Test
    void changePassword_ValidationFailed_ExceptionThrown() throws Exception {
        ChangePasswordCommand command = new ChangePasswordCommand(null);
        mockMvc.perform(MockMvcRequestBuilders.patch("/patients/abc@df.com")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("One or more fields have validation errors"));
        verifyNoInteractions(patientService);
    }

    @Test
    void changePassword_PatientNotFound_ExceptionThrown() throws Exception {
        String email = "abc@df.com";
        ChangePasswordCommand command = new ChangePasswordCommand("password");
        when(patientService.changePatientPassword(email, command.password()))
                .thenThrow(new PatientNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.patch("/patients/abc@df.com")
                        .content(objectMapper.writeValueAsString(command))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"));
        verify(patientService).changePatientPassword(email, command.password());
        verifyNoMoreInteractions(patientService);
    }
}

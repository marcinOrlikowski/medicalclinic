package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.dto.CreatePatientCommand;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.dto.PatientDto;
import com.marcinorlikowski.medicalclinic.exceptions.PatientNotFoundException;
import com.marcinorlikowski.medicalclinic.mapper.PatientMapper;
import com.marcinorlikowski.medicalclinic.model.Patient;
import com.marcinorlikowski.medicalclinic.model.User;
import com.marcinorlikowski.medicalclinic.repository.PatientRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PatientServiceTest {
    PatientService patientService;
    PatientMapper patientMapper;
    PatientRepository patientRepository;
    UserService userService;

    @BeforeEach
    void setup() {
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.userService = Mockito.mock(UserService.class);
        this.patientService = new PatientService(patientMapper, patientRepository, userService);
    }

    @Test
    void getAll_DataCorrect_PageReturned() {
        // given
        PageRequest pageable = PageRequest.of(0, 20);
        Page<Patient> patientPage = getPatientPage();
        when(patientRepository.findAll(pageable))
                .thenReturn(patientPage);
        // when
        PageDto<PatientDto> result = patientService.getAll(pageable);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(2, result.content().size()),
                () -> Assertions.assertEquals(1L, result.content().get(0).id()),
                () -> Assertions.assertEquals(2L, result.content().get(1).id()),
                () -> Assertions.assertEquals("abc@df.com", result.content().get(0).email()),
                () -> Assertions.assertEquals("abc2@df.com", result.content().get(1).email()),
                () -> Assertions.assertEquals("123", result.content().get(0).idCardNo()),
                () -> Assertions.assertEquals("123", result.content().get(1).idCardNo()),
                () -> Assertions.assertEquals("Sebek", result.content().get(0).firstName()),
                () -> Assertions.assertEquals("Drugi", result.content().get(1).firstName()),
                () -> Assertions.assertEquals("Javowy", result.content().get(0).lastName()),
                () -> Assertions.assertEquals("Sebek", result.content().get(1).lastName()),
                () -> Assertions.assertEquals("123456789", result.content().get(0).phoneNumber()),
                () -> Assertions.assertEquals("123456789", result.content().get(1).phoneNumber()),
                () -> Assertions.assertEquals(LocalDate.of(1999, 11, 6), result.content().get(0).birthDate()),
                () -> Assertions.assertEquals(LocalDate.of(1999, 11, 6), result.content().get(1).birthDate())
        );
    }

    @Test
    void getPatientByEmail_DataCorrect_PatientDtoReturned() {
        // given
        String email = "abc@df.com";
        User user = new User(1L, "Sebek", "Javowy", null, null);
        Patient patient = new Patient(1L, "abc@df.com", "pass", "123", user,
                "123456789", LocalDate.of(1999, 11, 6), Collections.emptyList());
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));
        // when
        PatientDto result = patientService.getPatientByEmail(email);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(email, result.email()),
                () -> Assertions.assertEquals("123", result.idCardNo()),
                () -> Assertions.assertEquals("Sebek", result.firstName()),
                () -> Assertions.assertEquals("Javowy", result.lastName()),
                () -> Assertions.assertEquals("123456789", result.phoneNumber()),
                () -> Assertions.assertEquals(LocalDate.of(1999, 11, 6), result.birthDate())
        );
    }

    @Test
    void getPatientByEmail_PatientNotFound_ExceptionThrown() {
        // given
        String email = "abc@df.com";
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        // when
        // then
        Assertions.assertThrows(PatientNotFoundException.class,
                () -> patientService.getPatientByEmail(email));
    }

    @Test
    void getPatientsByLastName_DataCorrect_ListOfPatientDtoReturned() {
        // given
        String lastName = "Javowy";
        User user = new User(1L, "Sebek", "Javowy", null, null);
        Patient patient = new Patient(1L, "abc@df.com", "pass", "123", user,
                "123456789", LocalDate.of(1999, 11, 6), Collections.emptyList());
        List<Patient> patients = List.of(patient);
        when(patientRepository.findByUserLastNameStartingWithIgnoreCase(lastName)).thenReturn(patients);
        // when
        List<PatientDto> result = patientService.getPatientsByLastName(lastName);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals("abc@df.com", result.getFirst().email()),
                () -> Assertions.assertEquals("123", result.getFirst().idCardNo()),
                () -> Assertions.assertEquals("Sebek", result.getFirst().firstName()),
                () -> Assertions.assertEquals(lastName, result.getFirst().lastName()),
                () -> Assertions.assertEquals("123456789", result.getFirst().phoneNumber()),
                () -> Assertions.assertEquals(LocalDate.of(1999, 11, 6), result.getFirst().birthDate())
        );
    }

    @Test
    void addPatient_DataCorrect_PatientAddedAndPatientDtoReturned() {
        // given
        CreatePatientCommand command = new CreatePatientCommand(
                "abc@df.com", "pass", "123", "Sebek", "Javowy",
                "123456789", LocalDate.of(1999, 11, 6)
        );
        User user = new User(1L, "Sebek", "Javowy", null, null);
        Patient patient = new Patient(command);
        patient.addUser(user);
        when(userService.getOrCreateUser("Sebek", "Javowy")).thenReturn(user);
        when(patientRepository.save(any()))
                .thenReturn(patient);
        // when
        PatientDto result = patientService.addPatient(command);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals("abc@df.com", result.email()),
                () -> Assertions.assertEquals("123", result.idCardNo()),
                () -> Assertions.assertEquals("Sebek", result.firstName()),
                () -> Assertions.assertEquals("Javowy", result.lastName()),
                () -> Assertions.assertEquals("123456789", result.phoneNumber()),
                () -> Assertions.assertEquals(LocalDate.of(1999, 11, 6), result.birthDate())
        );
        verify(userService).getOrCreateUser("Sebek", "Javowy");
        verify(patientRepository).save(any(Patient.class));
    }


    @Test
    void removePatientByEmail_DataCorrect_PatientRemoved() {
        // given
        String email = "abc@df.com";
        User user = new User(1L, "Sebek", "Javowy", null, null);
        Patient patient = new Patient();
        patient.setUser(user);
        when(patientRepository.findByEmail(email))
                .thenReturn(Optional.of(patient));
        // when
        patientService.removePatientByEmail(email);
        // then
        Assertions.assertNull(patient.getUser());
        verify(patientRepository).findByEmail(email);
        verify(patientRepository).delete(any());
    }

    @Test
    void removePatientByEmail_PatientNotFound_ExceptionThrown() {
        // given
        String email = "abc@df.com";
        when(patientRepository.findByEmail(email))
                .thenReturn(Optional.empty());
        // when
        // then
        Assertions.assertThrows(PatientNotFoundException.class,
                () -> patientService.removePatientByEmail(email));
    }

    @Test
    void updatePatient_DataCorrect_PatientUpdatedAndPatientDtoReturned() {
        // given
        String email = "abc@df.com";
        CreatePatientCommand command = new CreatePatientCommand(
                "abc@df.com", "pass", "123", "Sebek", "Javowy",
                "123456789", LocalDate.of(1999, 11, 6)
        );
        User oldUser = new User();
        User newUser = new User(2L, "Sebek", "Javowy", null, null);
        Patient patient = new Patient();
        patient.addUser(oldUser);

        when(patientRepository.findByEmail(email))
                .thenReturn(Optional.of(patient));
        when(userService.getOrCreateUser("Sebek", "Javowy"))
                .thenReturn(newUser);
        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // when
        PatientDto result = patientService.updatePatient(email, command);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals("Sebek", result.firstName()),
                () -> Assertions.assertEquals("Javowy", result.lastName()),
                () -> Assertions.assertEquals("123456789", result.phoneNumber()),
                () -> Assertions.assertEquals("123", result.idCardNo()),
                () -> Assertions.assertEquals(LocalDate.of(1999, 11, 6), result.birthDate())
        );
        verify(patientRepository).findByEmail(email);
        verify(patientRepository).save(any(Patient.class));
        verify(userService).getOrCreateUser("Sebek", "Javowy");
    }

    @Test
    void updatePatient_PatientNotFound_ExceptionThrown() {
        // given
        String email = "abc@df.com";
        CreatePatientCommand command = new CreatePatientCommand(
                "abc@df.com", "pass", "123", "Sebek", "Javowy",
                "123456789", LocalDate.of(1999, 11, 6)
        );
        when(patientRepository.findByEmail(email))
                .thenReturn(Optional.empty());
        // when
        // then
        Assertions.assertThrows(PatientNotFoundException.class,
                () -> patientService.updatePatient(email, command));
    }

    @Test
    void changePatientPassword_DataCorrect_PasswordChanged() {
        // given
        String email = "abc@df.com";
        String password = "newPassword";
        Patient patient = new Patient();
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // when
        patientService.changePatientPassword(email, password);
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(password, patient.getPassword())
        );
        verify(patientRepository).findByEmail(email);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void changePatientPassword_PatientNotFound_ExceptionThrown() {
        // given
        String email = "abc@df.com";
        String password = "newPassword";
        when(patientRepository.findByEmail(email))
                .thenReturn(Optional.empty());
        // when
        // then
        Assertions.assertThrows(PatientNotFoundException.class,
                () -> patientService.changePatientPassword(email, password));
    }

    private static Page<Patient> getPatientPage() {
        User user = new User(1L, "Sebek", "Javowy", null, null);
        User user2 = new User(2L, "Drugi", "Sebek", null, null);
        Patient patient = new Patient(
                1L, "abc@df.com", "pass", "123", user, "123456789",
                LocalDate.of(1999, 11, 6), Collections.emptyList()
        );
        Patient patient2 = new Patient(
                2L, "abc2@df.com", "pass", "123", user2, "123456789",
                LocalDate.of(1999, 11, 6), Collections.emptyList()
        );
        List<Patient> patients = List.of(patient, patient2);
        return new PageImpl<>(patients);
    }
}

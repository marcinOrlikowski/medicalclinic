package com.marcinorlikowski.medicalclinic.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthDate;

    public Patient(CreatePatientCommand command) {
        this.email = command.email();
        this.password = command.password();
        this.idCardNo = command.idCardNo();
        this.firstName = command.firstName();
        this.lastName = command.lastName();
        this.phoneNumber = command.phoneNumber();
        this.birthDate = command.birthDate();
    }

    public void updatePatient(Patient updatedPatient) {
        this.password = updatedPatient.password;
        this.idCardNo = updatedPatient.idCardNo;
        this.firstName = updatedPatient.firstName;
        this.lastName = updatedPatient.lastName;
        this.phoneNumber = updatedPatient.phoneNumber;
        this.birthDate = updatedPatient.birthDate;
    }
}

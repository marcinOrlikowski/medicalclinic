package com.marcinorlikowski.medicalclinic.model;

import com.marcinorlikowski.medicalclinic.dto.CreatePatientCommand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String idCardNo;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private String phoneNumber;
    private LocalDate birthDate;

    public Patient(CreatePatientCommand command) {
        this.email = command.email();
        this.password = command.password();
        this.idCardNo = command.idCardNo();
        this.phoneNumber = command.phoneNumber();
        this.birthDate = command.birthDate();
    }

    public void updatePatient(CreatePatientCommand command) {
        this.password = command.password();
        this.idCardNo = command.idCardNo();
        this.phoneNumber = command.phoneNumber();
        this.birthDate = command.birthDate();
    }

    public void addUser(User user) {
        this.user = user;
        user.setPatient(this);
    }

    public void removeUser(User user) {
        this.user = null;
        user.setPatient(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient))
            return false;
        Patient other = (Patient) o;
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

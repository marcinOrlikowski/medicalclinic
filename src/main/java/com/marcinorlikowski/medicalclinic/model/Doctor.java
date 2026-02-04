package com.marcinorlikowski.medicalclinic.model;

import com.marcinorlikowski.medicalclinic.dto.CreateDoctorCommand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Enumerated(value = EnumType.STRING)
    private Specialization specialization;
    @ManyToMany
    @JoinTable(
            name = "DOCTOR_INSTITUTION",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "institution_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "institution_id"}))
    private List<Institution> institutions = new ArrayList<>();
    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();


    public Doctor(CreateDoctorCommand command) {
        this.email = command.email();
        this.password = command.password();
        this.specialization = Specialization.from(command.specialization());
    }

    public void updateDoctor(CreateDoctorCommand command) {
        this.email = command.email();
        this.password = command.password();
        this.specialization = Specialization.from(command.specialization());
    }

    public void addUser(User user) {
        this.user = user;
        user.setDoctor(this);
    }

    public void removeUser(User user) {
        this.user = null;
        user.setDoctor(null);
    }

    public void assignInstitution(Institution institution) {
        this.institutions.add(institution);
        institution.getDoctors().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor))
            return false;
        Doctor other = (Doctor) o;
        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

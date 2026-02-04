package com.marcinorlikowski.medicalclinic.model;

import com.marcinorlikowski.medicalclinic.dto.CreateAppointmentCommand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "APPOINTMENTS")
@Getter
@Setter
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private Period period;
    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;

    public Appointment(CreateAppointmentCommand command) {
        this.period = Period.of(command.startDate(), command.endDate());
    }
}

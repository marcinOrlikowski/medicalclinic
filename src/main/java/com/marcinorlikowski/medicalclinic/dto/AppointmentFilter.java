package com.marcinorlikowski.medicalclinic.dto;

import com.marcinorlikowski.medicalclinic.model.Specialization;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppointmentFilter {
    private Long doctorId;
    private Long patientId;
    private Specialization specialization;
    private LocalDate date;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isAvailable;
}

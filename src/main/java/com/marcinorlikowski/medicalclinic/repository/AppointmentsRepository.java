package com.marcinorlikowski.medicalclinic.repository;

import com.marcinorlikowski.medicalclinic.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByDoctorId(Long doctorId);

    Page<Appointment> findAllByDoctorId(Pageable pageable, Long doctorId);

    Page<Appointment> findAllByPatientId(Pageable pageable, Long patientId);
}

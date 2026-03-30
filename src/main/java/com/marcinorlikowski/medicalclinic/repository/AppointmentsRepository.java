package com.marcinorlikowski.medicalclinic.repository;

import com.marcinorlikowski.medicalclinic.model.Appointment;
import com.marcinorlikowski.medicalclinic.model.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByDoctorId(Long doctorId);

    @Query("""
                SELECT count(a) > 0
                FROM Appointment a
                WHERE a.doctor.id = :doctorId
                AND a.period.start < :end
                AND :start < a.period.end
            """)
    boolean existsOverlappingAppointment(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
                 SELECT a
                 FROM Appointment a
                 WHERE a.doctor.specialization = :specialization
                 AND a.period.start >= :start
                 AND a.period.start <= :end
                 AND a.patient IS NULL
            """)
    Page<Appointment> findAvailableBySpecializationAndDate(
            Pageable pageable,
            @Param("specialization") Specialization specialization,
            @Param("start") LocalDateTime startDate,
            @Param("end") LocalDateTime endDate
    );

    Page<Appointment> findAllByDoctorId(Pageable pageable, Long doctorId);

    Page<Appointment> findByDoctorIdAndPatientIsNull(Pageable pageable, Long doctorId);

    Page<Appointment> findAllByPatientId(Pageable pageable, Long patientId);
}

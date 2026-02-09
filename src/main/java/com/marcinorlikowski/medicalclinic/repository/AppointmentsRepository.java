package com.marcinorlikowski.medicalclinic.repository;

import com.marcinorlikowski.medicalclinic.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByDoctorId(Long doctorId);

    @Query("""
                select count(a) > 0
                from Appointment a
                where a.doctor.id = :doctorId
                  and a.appointmentPeriod.start < :end
                  and :start < a.appointmentPeriod.end
            """)
    boolean existsOverlappingAppointment(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    Page<Appointment> findAllByDoctorId(Pageable pageable, Long doctorId);

    Page<Appointment> findAllByPatientId(Pageable pageable, Long patientId);
}

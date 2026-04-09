package com.marcinorlikowski.medicalclinic.repository;

import com.marcinorlikowski.medicalclinic.model.Appointment;
import com.marcinorlikowski.medicalclinic.model.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentsRepository extends
        JpaRepository<Appointment, Long>,
        JpaSpecificationExecutor<Appointment> {
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

    Page<Appointment> findAllByDoctorId(Pageable pageable, Long doctorId);

    Page<Appointment> findAllByPatientId(Pageable pageable, Long patientId);

    interface AppointmentSpecs {

        static Specification<Appointment> byIsAvailable(Boolean isAvailable) {
            return (root, query, builder) -> {
                if (isAvailable) {
                    return builder.isNull(root.get("patient"));
                } else {
                    return builder.isNotNull(root.get("patient"));
                }
            };
        }

        static Specification<Appointment> byDoctorId(Long doctorId) {
            return (root, query, builder) ->
                    builder.equal(root.get("doctor").get("id"), doctorId);
        }

        static Specification<Appointment> byPatientId(Long patientId) {
            return (root, query, builder) ->
                    builder.equal(root.get("patient").get("id"), patientId);
        }

        static Specification<Appointment> bySpecialization(Specialization specialization) {
            return (root, query, builder) ->
                    builder.equal(root.join("doctor").get("specialization"), specialization);
        }

        static Specification<Appointment> byDate(LocalDate date) {
            return (root, query, builder) ->
                    builder.between(
                            root.get("period").get("start"),
                            date.atStartOfDay(),           // 00:00:00
                            date.atTime(LocalTime.MAX)     // 23:59:59.999999999
                    );
        }

        static Specification<Appointment> byStartDateAfter(LocalDateTime startDate) {
            return (root, query, builder) ->
                    builder.greaterThanOrEqualTo(root.get("period").get("start"), startDate);
        }

        static Specification<Appointment> byEndDateBefore(LocalDateTime endDate) {
            return (root, query, builder) ->
                    builder.lessThanOrEqualTo(root.get("period").get("start"), endDate);
        }
    }
}

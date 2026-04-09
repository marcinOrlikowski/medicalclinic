package com.marcinorlikowski.medicalclinic.repository;

import com.marcinorlikowski.medicalclinic.model.Doctor;
import com.marcinorlikowski.medicalclinic.model.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long>,
        JpaSpecificationExecutor<Doctor> {
    Optional<Doctor> findByEmail(String email);

    Page<Doctor> findBySpecialization(Pageable pageable, Specialization specialization);

    interface DoctorSpecs {
        static Specification<Doctor> bySpecialization(Specialization specialization) {
            return (root, query, builder) ->
                    builder.equal(root.get("specialization"), specialization);
        }
    }
}

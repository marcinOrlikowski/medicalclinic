package com.marcinorlikowski.medicalclinic.repository;

import com.marcinorlikowski.medicalclinic.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
}

package com.marcinorlikowski.medicalclinic.repository;

import com.marcinorlikowski.medicalclinic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);
}

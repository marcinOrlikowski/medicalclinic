package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.model.User;
import com.marcinorlikowski.medicalclinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getOrCreateUser(String firstName, String lastName) {
        Optional<User> user = userRepository.findByFirstNameAndLastNameIgnoreCase(firstName, lastName);
        return user.orElseGet(
                () -> userRepository.save(new User(firstName, lastName))
        );
    }
}

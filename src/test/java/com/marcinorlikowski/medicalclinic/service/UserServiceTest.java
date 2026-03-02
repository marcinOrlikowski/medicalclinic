package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.model.User;
import com.marcinorlikowski.medicalclinic.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void setup() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.userService = new UserService(userRepository);
    }

    @Test
    void getOrCreateUser_DataCorrect_UserReturned() {
        // given
        String firstName = "Sebek";
        String lastName = "Javowy";
        User user = new User(firstName, lastName);
        when(userRepository.findByFirstNameAndLastNameIgnoreCase(firstName, lastName))
                .thenReturn(Optional.of(user));
        // when
        User result = userService.getOrCreateUser(firstName, lastName);
        // then
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals("Sebek", result.getFirstName()),
                () -> Assertions.assertEquals("Javowy", result.getLastName())
        );
    }
}

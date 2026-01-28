package com.marcinorlikowski.medicalclinic.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InputValidator {
    public static void validateEmail(String email) {
        Objects.requireNonNull(email, "Email cannot be null");
        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }

    public static void validateLastName(String lastName) {
        Objects.requireNonNull(lastName, "LastName cannot be null");
        if (lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("LastName cannot be empty");
        }
    }

    public static void validatePassword(String password) {
        Objects.requireNonNull(password, "Password cannot be null");
        if (password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}

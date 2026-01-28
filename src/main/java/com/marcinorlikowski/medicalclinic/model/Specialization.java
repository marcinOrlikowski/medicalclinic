package com.marcinorlikowski.medicalclinic.model;

import java.util.Arrays;

public enum Specialization {
    GENERAL_PRACTITIONER,
    SURGEON,
    CARDIOLOGIST;

    public static Specialization from(String value) {
        return Arrays.stream(values())
                .filter(s -> s.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Specialization not found:"));
    }
}

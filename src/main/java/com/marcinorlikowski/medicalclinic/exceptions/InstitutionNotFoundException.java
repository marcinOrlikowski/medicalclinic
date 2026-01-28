package com.marcinorlikowski.medicalclinic.exceptions;

public class InstitutionNotFoundException extends RuntimeException {
    public InstitutionNotFoundException() {
        super("ERROR - Institution not found");
    }
}

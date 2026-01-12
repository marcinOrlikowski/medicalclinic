package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.model.*;
import com.marcinorlikowski.medicalclinic.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PatientDto> getAll() {
        return patientService.getAll();
    }

    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto getPatientByEmail(@PathVariable String email) {
        return patientService.getPatientByEmail(email);
    }

    @GetMapping("/lastname/{lastname}")
    @ResponseStatus(HttpStatus.OK)
    public List<PatientDto> getPatientsByLastName(@PathVariable String lastname) {
        return patientService.getPatientsByLastName(lastname);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody CreatePatientCommand command) {
        return patientService.addPatient(command);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePatient(@PathVariable String email) {
        patientService.removePatientByEmail(email);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto updatePatient(@RequestBody CreatePatientCommand command, @PathVariable String email) {
        return patientService.updatePatient(command, email);
    }

    @PatchMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto changePassword(@PathVariable String email, @RequestBody ChangePasswordCommand password) {
        return patientService.changePatientPassword(email, password.password());
    }
}

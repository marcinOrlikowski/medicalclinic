package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.model.Patient;
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
    public List<Patient> getAll() {
        return patientService.getAll();
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public Patient getPatientByEmail(@PathVariable String email) {
        return patientService.getPatientByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient addPatient(@RequestBody Patient patient) {
        return patientService.addPatient(patient);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public Patient removePatient(@PathVariable String email) {
        return patientService.removePatientByEmail(email);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public Patient updatePatient(@RequestBody Patient updatedPatient, @PathVariable String email) {
       return patientService.updatePatient(updatedPatient, email);
    }
}

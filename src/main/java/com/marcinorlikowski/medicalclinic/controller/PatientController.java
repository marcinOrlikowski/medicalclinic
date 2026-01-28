package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.dto.ChangePasswordCommand;
import com.marcinorlikowski.medicalclinic.dto.CreatePatientCommand;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.dto.PatientDto;
import com.marcinorlikowski.medicalclinic.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageDto<PatientDto> getAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return patientService.getAll(pageable);
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
    public PatientDto addPatient(@Valid @RequestBody CreatePatientCommand command) {
        return patientService.addPatient(command);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePatient(@PathVariable String email) {
        patientService.removePatientByEmail(email);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto updatePatient(@Valid @RequestBody CreatePatientCommand command, @PathVariable String email) {
        return patientService.updatePatient(email, command);
    }

    @PatchMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto changePassword(@PathVariable String email, @Valid @RequestBody ChangePasswordCommand password) {
        return patientService.changePatientPassword(email, password.password());
    }
}

package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinic.dto.CreateAppointmentCommand;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping
    public PageDto<AppointmentDto> getAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return appointmentService.getAll(pageable);
    }

    @GetMapping("/doctors/{doctorId}")
    public PageDto<AppointmentDto> getAllByDoctorId(@PageableDefault(size = 20, sort = "id") Pageable pageable, @PathVariable Long doctorId) {
        return appointmentService.getAllByDoctorId(pageable, doctorId);
    }

    @GetMapping("/patients/{patientId}")
    public PageDto<AppointmentDto> getAllByPatientId(@PageableDefault(size = 20, sort = "id") Pageable pageable, @PathVariable Long patientId) {
        return appointmentService.getAllByPatientId(pageable, patientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDto createAppointment(@Valid @RequestBody CreateAppointmentCommand command) {
        return appointmentService.createAppointment(command);
    }

    @PatchMapping("/{appointmentId}/patient/{patientId}")
    public AppointmentDto assignPatient(@PathVariable Long appointmentId, @PathVariable Long patientId) {
        return appointmentService.assignPatient(appointmentId, patientId);
    }
}

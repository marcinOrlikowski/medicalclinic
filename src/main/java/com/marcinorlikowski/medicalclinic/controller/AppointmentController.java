package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.dto.*;
import com.marcinorlikowski.medicalclinic.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Appointments",
        description = "Operations related to appointments"
)
@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(summary = "Get appointments by filters")
    @GetMapping
    public PageDto<AppointmentDto> getByFilters(
            @Parameter(description = "Page, number of items to be displayed and sorting method")
            @PageableDefault(size = 20, sort = "id") Pageable pageable,
            AppointmentFilter filter
    ) {
        return appointmentService.getByFilters(pageable, filter);
    }

    @Operation(summary = "Get all appointments by doctor id")
    @GetMapping("/doctor/{doctorId}")
    public PageDto<AppointmentDto> getAllByDoctorId(
            @Parameter(description = "Page, number of items to be displayed and sorting method")
            @PageableDefault(size = 20, sort = "id") Pageable pageable, @PathVariable Long doctorId) {
        return appointmentService.getAllByDoctorId(pageable, doctorId);
    }

    @Operation(summary = "Get all appointments by patient id")
    @GetMapping("/patient/{patientId}")
    public PageDto<AppointmentDto> getAllByPatientId(
            @Parameter(description = "Page, number of items to be displayed and sorting method")
            @PageableDefault(size = 20, sort = "id") Pageable pageable, @PathVariable Long patientId) {
        return appointmentService.getAllByPatientId(pageable, patientId);
    }

    @Operation(summary = "Create appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "409", description = "Appointment for this date already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDto createAppointment(@Valid @RequestBody CreateAppointmentCommand command) {
        log.info("Received POST /appointments request for doctorid: {}", command.doctorId());
        return appointmentService.createAppointment(command);
    }

    @Operation(summary = "Assign patient to appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient assigned to appointment",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class))}),
            @ApiResponse(responseCode = "404", description = "Patient or appointment not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "409", description = "Appointment is not available anymore",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PatchMapping
    public AppointmentDto assignPatient(@RequestBody AssignPatientToAppointmentCommand command) {
        log.info("Received PATCH /appointments request to add patient with Id: '{}' to appointment with Id: '{}'",
                command.appointmentId(), command.patientId());
        return appointmentService.assignPatient(command.appointmentId(), command.patientId());
    }

    @Operation(summary = "Delete appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient assigned to appointment",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class))}),
            @ApiResponse(responseCode = "404", description = "Appointment not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@RequestParam Long appointmentId) {
        log.info("Received DELETE /appointments request to delete appointment with Id: '{}'", appointmentId);
        appointmentService.deleteAppointment(appointmentId);
    }
}

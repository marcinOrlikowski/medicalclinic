package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.dto.*;
import com.marcinorlikowski.medicalclinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

@Tag(
        name = "Patients",
        description = "Operations related to patients"
)
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get all patients")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageDto<PatientDto> getAll(
            @Parameter(description = "Page, number of items to be displayed and sorting method")
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.info("Received GET /patients request)");
        return patientService.getAll(pageable);
    }

    @Operation(summary = "Get patient by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))}),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto getPatientByEmail(@PathVariable String email) {
        log.info("Received GET /patients/email request with email: {}", email);
        return patientService.getPatientByEmail(email);
    }

    @Operation(summary = "Get patients by last name")
    @ApiResponse(responseCode = "200", description = "Patients found",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PatientDto.class)))})
    @GetMapping("/lastname/{lastname}")
    @ResponseStatus(HttpStatus.OK)
    public List<PatientDto> getPatientsByLastName(@PathVariable String lastname) {
        log.info("Received GET /patients/lastname request with lastname: {}", lastname);
        return patientService.getPatientsByLastName(lastname);
    }

    @Operation(summary = "Add patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "409", description = "Patient with this email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@Valid @RequestBody CreatePatientCommand command) {
        log.info("Received POST /patients request with email: {}", command.email());
        return patientService.addPatient(command);
    }

    @Operation(summary = "Delete patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePatient(@PathVariable String email) {
        log.info("Received DELETE /patients request with email: {}", email);
        patientService.removePatientByEmail(email);
    }

    @Operation(summary = "Update patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto updatePatient(@Valid @RequestBody CreatePatientCommand command, @PathVariable String email) {
        log.info("Received PUT /patients request with email: {}", email);
        return patientService.updatePatient(email, command);
    }

    @Operation(summary = "Change patient's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient's password changed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PatchMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto changePassword(@PathVariable String email, @Valid @RequestBody ChangePasswordCommand command) {
        log.info("Received PATCH /patients request with email: {}", email);
        return patientService.changePatientPassword(email, command.password());
    }
}

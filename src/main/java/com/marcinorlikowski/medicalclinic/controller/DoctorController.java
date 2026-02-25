package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.dto.*;
import com.marcinorlikowski.medicalclinic.service.DoctorService;
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
        name = "Doctors",
        description = "Operations related to doctors"
)
@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Get all doctors")
    @GetMapping
    public PageDto<DoctorDto> getAll(
            @Parameter(description = "Page, number of items to be displayed and sorting method")
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.info("Received GET /doctors request)");
        return doctorService.getAll(pageable);
    }

    @Operation(summary = "Add doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDto.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "409", description = "Doctor with this email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addDoctor(@Valid @RequestBody CreateDoctorCommand command) {
        log.info("Received POST /doctors request with email: {}", command.email());
        return doctorService.addDoctor(command);
    }

    @Operation(summary = "Delete doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Doctor deleted"),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @DeleteMapping("/{doctorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDoctor(@PathVariable Long doctorId) {
        log.info("Received DELETE /doctors request with id: {}", doctorId);
        doctorService.removeDoctor(doctorId);
    }

    @Operation(summary = "Update doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDto.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PutMapping("/{doctorId}")
    public DoctorDto updateDoctor(@PathVariable Long doctorId,
                                  @Valid @RequestBody CreateDoctorCommand command) {
        log.info("Received PUT /doctors request with id: {}", doctorId);
        return doctorService.updateDoctor(doctorId, command);
    }
}

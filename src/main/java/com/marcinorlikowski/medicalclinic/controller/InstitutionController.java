package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.dto.*;
import com.marcinorlikowski.medicalclinic.service.InstitutionService;
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
        name = "Institutions",
        description = "Operations related to institutions"
)
@RestController
@RequestMapping("/institutions")
@RequiredArgsConstructor
@Slf4j
public class InstitutionController {
    private final InstitutionService institutionService;

    @Operation(summary = "Get all institutions")
    @GetMapping
    public PageDto<InstitutionDto> getAll(
            @Parameter(description = "Page, number of items to be displayed and sorting method")
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        log.info("Received GET /institutions request)");
        return institutionService.getAll(pageable);
    }

    @Operation(summary = "Add institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Institution added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InstitutionDto.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "409", description = "Institution with this email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstitutionDto addInstitution(@Valid @RequestBody CreateInstitutionCommand command) {
        log.info("Received POST /institutions request with name: {}", command.name());
        return institutionService.addInstitution(command);
    }

    @Operation(summary = "Delete institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Institution deleted"),
            @ApiResponse(responseCode = "404", description = "Institution not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @DeleteMapping("/{institutionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInstitution(@PathVariable Long institutionId) {
        log.info("Received DELETE /institutions request with id: {}", institutionId);
        institutionService.deleteInstitution(institutionId);
    }

    @Operation(summary = "Update institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Institution updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InstitutionDto.class))}),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "404", description = "Institution not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PutMapping("/{institutionId}")
    public InstitutionDto updateInstitution(@PathVariable Long institutionId,
                                            @Valid @RequestBody CreateInstitutionCommand command) {
        log.info("Received PUT /institutions request with id: {}", institutionId);
        return institutionService.updateInstitution(institutionId, command);
    }

    @Operation(summary = "Add Doctor to institution")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Doctor added to institution",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InstitutionDto.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor or institution not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "409", description = "This doctor is already assigned to this institution",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class)))})
    @PostMapping("/{institutionId}/doctors/{doctorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public InstitutionDto addDoctorToInstitution(@PathVariable Long institutionId, @PathVariable Long doctorId) {
        log.info("Received POST /institutions/{}/doctors/{} request to add doctor to institution",
                institutionId, doctorId);
        return institutionService.addDoctorToInstitution(institutionId, doctorId);
    }
}

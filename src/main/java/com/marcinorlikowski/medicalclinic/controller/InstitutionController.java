package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.dto.CreateInstitutionCommand;
import com.marcinorlikowski.medicalclinic.dto.InstitutionDto;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.service.InstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/institutions")
@RequiredArgsConstructor
public class InstitutionController {
    private final InstitutionService institutionService;

    @GetMapping
    public PageDto<InstitutionDto> getAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return institutionService.getAll(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstitutionDto addInstitution(@Valid @RequestBody CreateInstitutionCommand command) {
        return institutionService.addInstitution(command);
    }

    @DeleteMapping("/{institutionId}")
    public void deleteInstitution(@PathVariable Long institutionId) {
        institutionService.deleteInstitution(institutionId);
    }

    @PutMapping("/{institutionId}")
    public InstitutionDto updateInstitution(@PathVariable Long institutionId,
                                            @Valid @RequestBody CreateInstitutionCommand command) {
        return institutionService.updateInstitution(institutionId, command);
    }

    @PostMapping("/{institutionId}/doctors/{doctorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public InstitutionDto addDoctorToInstitution(@PathVariable Long institutionId, @PathVariable Long doctorId) {
        return institutionService.addDoctorToInstitution(institutionId, doctorId);
    }
}

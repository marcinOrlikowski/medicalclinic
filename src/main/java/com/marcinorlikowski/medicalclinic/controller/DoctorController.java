package com.marcinorlikowski.medicalclinic.controller;

import com.marcinorlikowski.medicalclinic.dto.CreateDoctorCommand;
import com.marcinorlikowski.medicalclinic.dto.DoctorDto;
import com.marcinorlikowski.medicalclinic.dto.PageDto;
import com.marcinorlikowski.medicalclinic.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping
    public PageDto<DoctorDto> getAll(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return doctorService.getAll(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto addDoctor(@RequestBody CreateDoctorCommand command) {
        return doctorService.addDoctor(command);
    }

    @DeleteMapping("/{doctorId}")
    public void removeDoctor(@PathVariable Long doctorId) {
        doctorService.removeDoctor(doctorId);
    }

    @PutMapping("/{doctorId}")
    public DoctorDto updateDoctor(@PathVariable Long doctorId,
                                  @Valid @RequestBody CreateDoctorCommand command) {
        return doctorService.updateDoctor(doctorId, command);
    }
}

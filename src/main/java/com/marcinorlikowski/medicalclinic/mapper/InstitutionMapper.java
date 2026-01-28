package com.marcinorlikowski.medicalclinic.mapper;

import com.marcinorlikowski.medicalclinic.model.Institution;
import com.marcinorlikowski.medicalclinic.dto.InstitutionDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = DoctorMapper.class)
public interface InstitutionMapper {
    InstitutionDto toDto(Institution institution);

    List<InstitutionDto> toDto(List<Institution> institutions);
}

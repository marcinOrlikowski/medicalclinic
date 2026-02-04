package com.marcinorlikowski.medicalclinic.mapper;

import com.marcinorlikowski.medicalclinic.dto.AppointmentDto;
import com.marcinorlikowski.medicalclinic.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "startDate", source = "period.start")
    @Mapping(target = "endDate", source = "period.end")
    AppointmentDto toDto(Appointment appointment);

    List<AppointmentDto> toDto(List<Appointment> appointments);
}

package com.marcinorlikowski.medicalclinic.service;

import com.marcinorlikowski.medicalclinic.dto.*;
import com.marcinorlikowski.medicalclinic.exceptions.*;
import com.marcinorlikowski.medicalclinic.mapper.AppointmentMapper;
import com.marcinorlikowski.medicalclinic.model.Appointment;
import com.marcinorlikowski.medicalclinic.model.Doctor;
import com.marcinorlikowski.medicalclinic.model.Patient;
import com.marcinorlikowski.medicalclinic.repository.AppointmentsRepository;
import com.marcinorlikowski.medicalclinic.repository.DoctorRepository;
import com.marcinorlikowski.medicalclinic.repository.PatientRepository;
import com.marcinorlikowski.medicalclinic.utils.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.marcinorlikowski.medicalclinic.repository.AppointmentsRepository.AppointmentSpecs.*;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    private final AppointmentsRepository appointmentsRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper mapper;

    public PageDto<AppointmentDto> getAll(Pageable pageable) {
        log.info("Getting appointments page: {}, with {} elements", pageable.getPageNumber(), pageable.getPageSize());
        Page<Appointment> appointments = appointmentsRepository.findAll(pageable);
        List<AppointmentDto> appointmentsDto = mapper.toDto(appointments.getContent());
        PageMetadata metadata = createPageMetaData(appointments);
        return new PageDto<>(appointmentsDto, metadata);
    }

    public PageDto<AppointmentDto> getByFilters(
            Pageable pageable,
            AppointmentFilter filter
    ) {
        log.info("Getting appointments with filters: {}, page: {}, with {} elements", filter, pageable.getPageNumber(), pageable.getPageSize());
        Specification<Appointment> spec = createSpecs(filter);
        Page<Appointment> appointments = appointmentsRepository.findAll(spec, pageable);
        List<AppointmentDto> appointmentsDto = mapper.toDto(appointments.getContent());
        PageMetadata metadata = createPageMetaData(appointments);
        return new PageDto<>(appointmentsDto, metadata);
    }

    public PageDto<AppointmentDto> getAllByDoctorId(Pageable pageable, Long doctorId) {
        log.info("Getting appointments page: {}, with {} elements for doctor with id: {}",
                pageable.getPageNumber(), pageable.getPageSize(), doctorId);
        Page<Appointment> appointments = appointmentsRepository.findAllByDoctorId(pageable, doctorId);
        List<AppointmentDto> appointmentsDto = mapper.toDto(appointments.getContent());
        PageMetadata metadata = createPageMetaData(appointments);
        return new PageDto<>(appointmentsDto, metadata);
    }

    public PageDto<AppointmentDto> getAllByPatientId(Pageable pageable, Long patientId) {
        log.info("Getting appointments page: {}, with {} elements for patient with id: {}",
                pageable.getPageNumber(), pageable.getPageSize(), patientId);
        Page<Appointment> appointments = appointmentsRepository.findAllByPatientId(pageable, patientId);
        List<AppointmentDto> appointmentsDto = mapper.toDto(appointments.getContent());
        PageMetadata metadata = createPageMetaData(appointments);
        return new PageDto<>(appointmentsDto, metadata);
    }

    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentCommand command) {
        Validator.validateDateTime(command.startDate(), command.endDate());
        Doctor doctor = doctorRepository.findById(command.doctorId())
                .orElseThrow(DoctorNotFoundException::new);
        checkIfAppointmentOverlaps(command);
        Appointment appointment = new Appointment(command);
        doctor.addAppointment(appointment);
        Appointment saved = appointmentsRepository.save(appointment);
        log.info("Appointment successfully added for doctor with id: {}", command.doctorId());
        return mapper.toDto(saved);
    }

    @Transactional
    public AppointmentDto assignPatient(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentsRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);
        Validator.validateIfAppointmentIsAvailable(appointment);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(PatientNotFoundException::new);
        patient.assignPatientToAppointment(appointment);
        Appointment saved = appointmentsRepository.save(appointment);
        log.info("Patient with id: {}, successfully added to appointment with id: {}", patientId, appointmentId);
        return mapper.toDto(saved);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId) {
        Appointment appointment = appointmentsRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();
        if (nonNull(patient) && nonNull(doctor)) {
            patient.detachPatientFromAppointment(appointment);
            doctor.removeAppointment(appointment);
        }
        appointmentsRepository.delete(appointment);
        log.info("Appointment with id: '{}', successfully deleted", appointmentId);
    }

    private void checkIfAppointmentOverlaps(CreateAppointmentCommand command) {
        boolean overlaps = appointmentsRepository.existsOverlappingAppointment(
                command.doctorId(), command.startDate(), command.endDate()
        );
        if (overlaps) {
            throw new AppointmentNotAvailableException();
        }
    }

    private PageMetadata createPageMetaData(Page<Appointment> appointments) {
        return new PageMetadata(
                appointments.getNumber(),
                appointments.getSize(),
                appointments.getTotalElements(),
                appointments.getTotalPages()
        );
    }

    private Specification<Appointment> createSpecs(AppointmentFilter filter) {
        Specification<Appointment> spec = Specification.unrestricted();
        if (filter.getIsAvailable() != null) {
            spec = spec.and(byIsAvailable(filter.getIsAvailable()));
        }
        if (filter.getDoctorId() != null) {
            spec = spec.and(byDoctorId(filter.getDoctorId()));
        }
        if (filter.getPatientId() != null) {
            spec = spec.and(byPatientId(filter.getPatientId()));
        }
        if (filter.getSpecialization() != null) {
            spec = spec.and(bySpecialization(filter.getSpecialization()));
        }
        if (filter.getDate() != null) {
            spec = spec.and(byDate(filter.getDate()));
        } else {
            if (filter.getStartDate() != null) {
                spec = spec.and(byStartDateAfter(filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                spec = spec.and(byEndDateBefore(filter.getEndDate()));
            }
        }
        return spec;
    }
}

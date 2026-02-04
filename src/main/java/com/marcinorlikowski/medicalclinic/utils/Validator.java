package com.marcinorlikowski.medicalclinic.utils;

import com.marcinorlikowski.medicalclinic.exceptions.AppointmentNotAvailableException;
import com.marcinorlikowski.medicalclinic.exceptions.InvalidDateTimeException;
import com.marcinorlikowski.medicalclinic.exceptions.PatientAlreadyAssignedException;
import com.marcinorlikowski.medicalclinic.model.Appointment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validator {
    public static void validateIfAppointmentIsAvailable(Appointment appointment) {
        if (appointment.getPatient() != null) {
            throw new PatientAlreadyAssignedException();
        }
        if (appointment.getPeriod().start().isBefore(LocalDateTime.now())) {
            throw new AppointmentNotAvailableException();
        }
    }

    public static void validateDateTime(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new InvalidDateTimeException("ERROR - Start date must be in future");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateTimeException("ERROR - Start date must be before end date");
        }
        if (startDate.equals(endDate)) {
            throw new InvalidDateTimeException("ERROR - Start date is same as end date");
        }
        if (startDate.getMinute() % 15 != 0) {
            throw new InvalidDateTimeException("ERROR - Start time must be an quarter of an hour (ex. 14:15");
        }
    }
}

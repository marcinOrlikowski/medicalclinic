package com.marcinorlikowski.medicalclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public record Period(
        @Column(name = "start_date")
        LocalDateTime start,
        @Column(name = "end_date")
        LocalDateTime end) {
    public static Period of(LocalDateTime startDate, LocalDateTime endDate) {
        return new Period(startDate, endDate);
    }

    public boolean overlaps(Period other) {
        return other.start.isBefore(this.end)
                && this.start.isBefore(other.end);
    }
}

package com.marcinorlikowski.medicalclinic.dto;


public record PageMetadata(int page,
                           int size,
                           long totalElements,
                           int totalPages) {
}

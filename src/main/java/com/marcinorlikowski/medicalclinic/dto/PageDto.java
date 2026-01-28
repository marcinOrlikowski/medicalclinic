package com.marcinorlikowski.medicalclinic.dto;


import java.util.List;


public record PageDto<T>(List<T> content,
                         PageMetadata metaDate) {

}

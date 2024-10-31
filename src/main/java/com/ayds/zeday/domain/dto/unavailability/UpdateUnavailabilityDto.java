package com.ayds.zeday.domain.dto.unavailability;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateUnavailabilityDto(
        @JsonFormat(pattern = "HH:mm") Optional<LocalTime> startAt,
        @JsonFormat(pattern = "HH:mm") Optional<LocalTime> endAt,
        Optional<@Future LocalDate> specificDay) {
}

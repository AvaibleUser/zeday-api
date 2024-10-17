package com.ayds.zeday.domain.dto.unavailability;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import jakarta.validation.constraints.Future;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateUnavailabilityDto(
        Optional<LocalTime> startAt,
        Optional<LocalTime> endAt,
        Optional<@Future LocalDate> specificDay) {
}

package com.ayds.zeday.domain.dto.availability;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record AvailabilityDto(
        Long id,
        LocalTime startAt,
        LocalTime endAt,
        Boolean recurring,
        Optional<DayOfWeek> dayOfWeek,
        Optional<LocalDate> specificDay,
        Instant createdAt,
        Instant updatedAt) {
}

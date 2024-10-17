package com.ayds.zeday.domain.dto.availability;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import jakarta.validation.constraints.Future;

public record UpdateAvailabilityDto(
        Optional<LocalTime> startAt,
        Optional<LocalTime> endAt,
        Optional<DayOfWeek> dayOfWeek,
        Optional<@Future LocalDate> specificDay) {
}

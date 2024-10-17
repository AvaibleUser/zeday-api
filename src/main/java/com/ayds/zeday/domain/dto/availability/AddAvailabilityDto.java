package com.ayds.zeday.domain.dto.availability;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddAvailabilityDto(
        @NotNull @Future LocalTime startAt,
        @NotNull @Future LocalTime endAt,
        @NotNull Boolean recurring,
        Optional<DayOfWeek> dayOfWeek,
        Optional<@Future LocalDate> specificDay) {
}

package com.ayds.zeday.domain.dto.availability;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddAvailabilityDto(
        @JsonFormat(pattern = "HH:mm") Optional<LocalTime> startAt,
        @JsonFormat(pattern = "HH:mm") Optional<LocalTime> endAt,
        @NotNull DayOfWeek dayOfWeek) {
}

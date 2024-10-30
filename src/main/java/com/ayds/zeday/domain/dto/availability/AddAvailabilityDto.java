package com.ayds.zeday.domain.dto.availability;

import java.time.LocalTime;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddAvailabilityDto(
        Optional<LocalTime> startAt,
        Optional<LocalTime> endAt,
        @NotNull @Size(min = 1, max = 7) Integer dayOfWeek) {
}

package com.ayds.zeday.domain.dto.unavailability;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddUnavailabilityDto(
        @NotNull LocalTime startAt,
        @NotNull LocalTime endAt,
        @NotNull @Future LocalDate specificDay) {
}

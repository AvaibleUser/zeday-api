package com.ayds.zeday.domain.dto.unavailability;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddUnavailabilityDto(
        @JsonFormat(pattern = "HH:mm") @NotNull LocalTime startAt,
        @JsonFormat(pattern = "HH:mm") @NotNull LocalTime endAt,
        @NotNull @Future LocalDate specificDay) {
}

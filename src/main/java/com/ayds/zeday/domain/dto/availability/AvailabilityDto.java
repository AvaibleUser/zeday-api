package com.ayds.zeday.domain.dto.availability;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder(toBuilder = true)
public record AvailabilityDto(
        Long id,
        @JsonFormat(pattern = "HH:mm") LocalTime startAt,
        @JsonFormat(pattern = "HH:mm") LocalTime endAt,
        DayOfWeek dayOfWeek,
        Instant createdAt,
        Instant updatedAt) {
}

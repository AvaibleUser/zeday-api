package com.ayds.zeday.domain.dto.availability;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

import lombok.Builder;

@Builder(toBuilder = true)
public record AvailabilityDto(
        LocalTime startAt,
        LocalTime endAt,
        DayOfWeek dayOfWeek) {
}

package com.ayds.zeday.domain.dto.appointment;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddAppointmentDto(
        @NotNull Instant startAt,
        @NotNull Instant endAt,
        @NotNull long attendant) {
}

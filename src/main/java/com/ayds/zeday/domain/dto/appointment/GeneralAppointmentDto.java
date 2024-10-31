package com.ayds.zeday.domain.dto.appointment;

import java.time.Instant;

import com.ayds.zeday.domain.enums.AppointmentStateEnum;

import lombok.Builder;

@Builder(toBuilder = true)
public record GeneralAppointmentDto(
        Long id,
        Instant startAt,
        Instant endAt,
        AppointmentStateEnum state,
        String notes,
        Instant createdAt,
        Instant updatedAt) {

}

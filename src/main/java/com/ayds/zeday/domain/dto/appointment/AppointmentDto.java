package com.ayds.zeday.domain.dto.appointment;

import java.time.Instant;

import com.ayds.zeday.domain.dto.service.ServiceDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.enums.AppointmentStateEnum;

import lombok.Builder;

@Builder(toBuilder = true)
public record AppointmentDto(
        Long id,
        Instant startAt,
        Instant endAt,
        AppointmentStateEnum state,
        String notes,
        UserDto customer,
        ServiceDto service,
        UserDto attendant,
        Instant createdAt,
        Instant updatedAt) {
}

package com.ayds.zeday.domain.dto.appointment;

import java.time.Instant;
import java.time.LocalTime;

import com.ayds.zeday.domain.dto.service.ServiceDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.enums.AppointmentStateEnum;

import lombok.Builder;

@Builder(toBuilder = true)
public record AppointmentDto(
        Long id,
        LocalTime startAt,
        LocalTime endAt,
        AppointmentStateEnum state,
        String notes,
        UserDto customer,
        ServiceDto service,
        UserDto attendant,
        Instant createdAt,
        Instant updatedAt) {
}

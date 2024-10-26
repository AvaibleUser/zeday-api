package com.ayds.zeday.domain.dto.appointment;

import java.util.Optional;

import com.ayds.zeday.domain.enums.AppointmentStateEnum;

import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateAppointmentDto(
        Optional<AppointmentStateEnum> state,
        Optional<String> notes) {
}

package com.ayds.zeday.domain.dto.schedule;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import com.ayds.zeday.domain.dto.appointment.AppointmentDto;
import com.ayds.zeday.domain.dto.availability.AvailabilityDto;
import com.ayds.zeday.domain.dto.service.ServiceDto;

import lombok.Builder;

@Builder(toBuilder = true)
public record ScheduleDto(
        Long id,
        String title,
        Optional<String> notes,
        Set<AvailabilityDto> availabilities,
        Set<AppointmentDto> appointments,
        Set<ServiceDto> services,
        Instant createdAt,
        Instant updatedAt) {
}

package com.ayds.zeday.domain.dto.schedule;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record GeneralScheduleDto(
        Long id,
        String title,
        String notes,
        Instant createdAt,
        Instant updatedAt) {
}

package com.ayds.zeday.domain.dto.service;

import java.time.Duration;
import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record ServiceDto(
        String name,
        String description,
        Duration duration,
        Boolean cancellable,
        Double price,
        Integer maxDaysToCancel,
        Integer minDaysToSchedule,
        Integer maxDaysToSchedule,
        Integer advancePaymentPercentage) {
}

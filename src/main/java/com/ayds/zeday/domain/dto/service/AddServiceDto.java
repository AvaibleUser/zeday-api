package com.ayds.zeday.domain.dto.service;

import java.time.Duration;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddServiceDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive Duration duration,
        Optional<Boolean> cancellable,
        Optional<@PositiveOrZero Integer> maxDaysToCancel,
        Optional<@PositiveOrZero Integer> minDaysToSchedule,
        Optional<@PositiveOrZero Integer> maxDaysToSchedule,
        Optional<@PositiveOrZero Integer> advancePaymentPercentage) {
}

package com.ayds.zeday.domain.dto.service;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddServiceDto(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive Duration duration) {
}

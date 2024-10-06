package com.ayds.zeday.domain.dto.service;

import java.time.Duration;
import java.util.Optional;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateServiceDto(
        Optional<String> description,
        Optional<@Positive Duration> duration) {
}

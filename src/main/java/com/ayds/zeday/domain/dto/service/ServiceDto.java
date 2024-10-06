package com.ayds.zeday.domain.dto.service;

import java.time.Duration;

import lombok.Builder;

@Builder(toBuilder = true)
public record ServiceDto(
        Long id,
        String name,
        String description,
        Duration duration) {
}

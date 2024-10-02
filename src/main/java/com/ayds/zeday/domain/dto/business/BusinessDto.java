package com.ayds.zeday.domain.dto.business;

import java.time.Instant;

import lombok.Builder;

@Builder
public record BusinessDto(
        Long id,
        String name,
        Boolean autoAssignment,
        String logoUrl,
        Instant createdAt,
        Instant updatedAt) {
}

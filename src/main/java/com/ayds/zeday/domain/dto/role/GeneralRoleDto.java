package com.ayds.zeday.domain.dto.role;

import java.time.Instant;

import lombok.Builder;

@Builder(toBuilder = true)
public record GeneralRoleDto(
        Long id,
        String name,
        String description,
        Boolean multiuser,
        Instant createdAt,
        Instant updatedAt) {
}

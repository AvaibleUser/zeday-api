package com.ayds.zeday.domain.dto.role;

import java.time.Instant;
import java.util.Set;

import com.ayds.zeday.domain.dto.permission.PermissionDto;

public record RoleDto(
        Long id,
        String name,
        String description,
        Boolean multiuser,
        Set<PermissionDto> permissions,
        Instant createdAt,
        Instant updatedAt) {
}

package com.ayds.zeday.domain.dto.role;

import java.time.Instant;
import java.util.Set;

import com.ayds.zeday.domain.dto.permission.PermissionDto;

public interface RoleDto {

    Long getId();

    String getName();

    String getDescription();

    Boolean getMultiuser();

    Set<PermissionDto> getPermissions();

    Instant getCreatedAt();

    Instant getUpdatedAt();
}

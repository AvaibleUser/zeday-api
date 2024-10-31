package com.ayds.zeday.domain.dto.role;

import java.time.Instant;
import java.util.Set;

import com.ayds.zeday.domain.dto.permission.PermissionDto;

import lombok.Builder;
import lombok.Value;

public interface RoleDto {

    Long getId();

    String getName();

    String getDescription();

    Boolean getMultiuser();

    Set<PermissionDto> getPermissions();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    @Value
    @Builder(toBuilder = true)
    public static class RoleDtoImpl implements RoleDto {
        private Long id;
        private String name;
        private String description;
        private Boolean multiuser;
        private Instant createdAt;
        private Instant updatedAt;
        private Set<PermissionDto> permissions;

        @Override
        public Instant getUpdatedAt() {
            return updatedAt;
        }
    }
}

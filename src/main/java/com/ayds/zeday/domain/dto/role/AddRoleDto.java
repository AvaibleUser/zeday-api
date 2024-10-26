package com.ayds.zeday.domain.dto.role;

import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record AddRoleDto(
        String name,
        String description,
        Optional<Boolean> multiuser) {
}

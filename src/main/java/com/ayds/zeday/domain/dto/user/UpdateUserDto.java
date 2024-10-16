package com.ayds.zeday.domain.dto.user;

import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateUserDto(
        Optional<String> phone,
        Optional<String> currentPassword,
        Optional<String> newPassword) {
}

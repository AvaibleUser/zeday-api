package com.ayds.zeday.domain.dto.user;

import java.util.Optional;

public record UpdateUserDto(
        Optional<String> phone,
        Optional<String> currentPassword,
        Optional<String> newPassword) {
}

package com.ayds.zeday.domain.dto.user;

import jakarta.validation.constraints.NotBlank;

public record RecoverUserDto(
        @NotBlank String email) {
}

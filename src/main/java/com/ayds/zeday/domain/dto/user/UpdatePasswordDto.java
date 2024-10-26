package com.ayds.zeday.domain.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdatePasswordDto(
        @NotEmpty String password) {
}

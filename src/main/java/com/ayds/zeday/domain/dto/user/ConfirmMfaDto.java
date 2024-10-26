package com.ayds.zeday.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record ConfirmMfaDto(
        @NotBlank String code) {
}

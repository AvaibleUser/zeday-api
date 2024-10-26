package com.ayds.zeday.domain.dto.user;

import lombok.Builder;

@Builder(toBuilder = true)
public record MfaSecretDto(
        String qrUrl,
        String secret) {
}

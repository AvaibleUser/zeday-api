package com.ayds.zeday.domain.dto.user;

public record MfaUserDto(
    Long id,
    String mfaSecret,
    Boolean activeMfa) {
}

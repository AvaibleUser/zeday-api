package com.ayds.zeday.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdatePasswordDto(
        @NotBlank String email,
        @NotBlank String code,
        @JsonAlias("newPassword") @NotEmpty String password) {
}

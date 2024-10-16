package com.ayds.zeday.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddUserDto(
        @NotBlank String name,
        @NotBlank String lastname,
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String nit,
        @NotBlank String cui,
        @NotBlank String phone) {
}

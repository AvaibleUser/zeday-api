package com.ayds.zeday.domain.dto.business;

import java.util.Optional;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddBusinessDto(
        @NotBlank String name,
        Optional<Boolean> autoAssignment) {
}

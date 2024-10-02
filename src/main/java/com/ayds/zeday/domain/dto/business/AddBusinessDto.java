package com.ayds.zeday.domain.dto.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddBusinessDto(
        @NotBlank String name,
        @NotNull Boolean autoAssignment) {
}

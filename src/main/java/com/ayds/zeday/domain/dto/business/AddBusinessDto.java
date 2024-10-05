package com.ayds.zeday.domain.dto.business;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddBusinessDto(
        @NotBlank String name,
        @NotNull Boolean autoAssignment) {
}

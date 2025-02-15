package com.ayds.zeday.domain.dto.business;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateBusinessDto(
        @NotNull Boolean autoAssignment) {
}

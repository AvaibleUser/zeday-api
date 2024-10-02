package com.ayds.zeday.domain.dto.business;

import jakarta.validation.constraints.NotNull;

public record UpdateBusinessDto(
        @NotNull Boolean autoAssignment) {
}

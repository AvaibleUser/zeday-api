package com.ayds.zeday.domain.dto.schedule;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record AddScheduleDto(
        @NotBlank String title) {
}

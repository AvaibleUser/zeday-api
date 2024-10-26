package com.ayds.zeday.domain.dto.permission;

import java.time.Instant;

import com.ayds.zeday.domain.dto.schedule.GeneralScheduleDto;
import com.ayds.zeday.domain.enums.AccessEnum;

public record PermissionDto(
    Long id,
    String module,
    AccessEnum grandAccess,
    GeneralScheduleDto schedule,
    Instant createdAt,
    Instant updatedAt) {
}

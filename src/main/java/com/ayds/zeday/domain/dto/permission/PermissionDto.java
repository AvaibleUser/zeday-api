package com.ayds.zeday.domain.dto.permission;

import java.time.Instant;

import com.ayds.zeday.domain.dto.schedule.GeneralScheduleDto;
import com.ayds.zeday.domain.enums.AccessEnum;

public interface PermissionDto {

    Long getId();

    String getModule();

    AccessEnum getGrantAccess();

    GeneralScheduleDto getSchedule();

    Instant getCreatedAt();

    Instant getUpdatedAt();
}

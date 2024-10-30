package com.ayds.zeday.domain.dto.role;

import java.time.Instant;

public interface GeneralRoleDto {

    Long getId();

    String getName();

    String getDescription();

    Boolean getMultiuser();

    Instant getCreatedAt();

    Instant getUpdatedAt();
}

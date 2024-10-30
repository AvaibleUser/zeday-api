package com.ayds.zeday.domain.dto.user;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import com.ayds.zeday.domain.dto.role.GeneralRoleDto;

public interface UserRoleDto {

    Long getId();

    String getName();

    String getLastname();

    String getEmail();

    String getNit();

    String getCui();

    String getPhone();

    Boolean getActiveMfa();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    @Value("#{@userDtoFiller.getPermissions(target)}")
    List<String> getPermissions();

    Set<GeneralRoleDto> getRoles();
}

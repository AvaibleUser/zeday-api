package com.ayds.zeday.domain.dto.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

public interface MfaUserDto {

    Long getId();

    String getMfaSecret();

    Boolean getActiveMfa();

    @Value("#{@userDtoFiller.getPermissions(target)}")
    List<String> getPermissions();
}

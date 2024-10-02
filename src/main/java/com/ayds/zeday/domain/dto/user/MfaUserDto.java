package com.ayds.zeday.domain.dto.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.ayds.zeday.domain.entity.UserEntity;

@Projection(name = "public_mfa_user", types = { UserEntity.class })
public interface MfaUserDto {

    Long getId();

    String getMfaSecret();

    Boolean getActiveMfa();

    @Value("#{@userDtoFiller.getPermissions(target)}")
    List<String> getPermissions();
}

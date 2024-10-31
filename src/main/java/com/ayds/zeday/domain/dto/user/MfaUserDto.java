package com.ayds.zeday.domain.dto.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import lombok.Builder;
import lombok.Data;

public interface MfaUserDto {

    Long getId();

    String getName();

    String getMfaSecret();

    Boolean getActiveMfa();

    @Value("#{@userDtoFiller.getPermissions(target)}")
    List<String> getPermissions();

    @Data
    @Builder(toBuilder = true)
    public static class MfaUserDtoImpl implements MfaUserDto {

        private Long id;
        private String name;
        private String mfaSecret;
        private Boolean activeMfa;
        private List<String> permissions;
    }
}

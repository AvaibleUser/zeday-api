package com.ayds.zeday.domain.dto.user;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
import lombok.Builder;

public interface UserDto {

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

    @Data
    @Builder(toBuilder = true)
    public static class UserDtoImpl implements UserDto {
        private Long id;
        private String name;
        private String lastname;
        private String email;
        private String nit;
        private String cui;
        private String phone;
        private Boolean activeMfa;
        private Instant createdAt;
        private Instant updatedAt;
        private List<String> permissions;
    }
}

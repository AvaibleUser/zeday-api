package com.ayds.zeday.domain.dto.user;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import com.ayds.zeday.domain.dto.role.GeneralRoleDto;

import lombok.Builder;
import lombok.Data;

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

    @Data
    @Builder(toBuilder = true)
    class UserRoleDtoImpl implements UserRoleDto {

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
        private Set<GeneralRoleDto> roles;
    }
}

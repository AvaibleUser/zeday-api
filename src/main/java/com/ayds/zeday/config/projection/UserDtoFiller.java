package com.ayds.zeday.config.projection;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.ayds.zeday.domain.entity.UserEntity;

public class UserDtoFiller {

    public List<String> getPermissions(UserEntity user) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
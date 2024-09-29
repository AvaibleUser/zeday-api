package com.ayds.zeday.domain.dto.user;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.ayds.zeday.domain.entity.UserEntity;

@Component
class UserDtoFiller {

    List<String> getPermissions(UserEntity user) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}

@Projection(name = "public_user", types = { UserEntity.class })
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

}

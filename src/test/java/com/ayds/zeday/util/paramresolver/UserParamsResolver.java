package com.ayds.zeday.util.paramresolver;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import com.ayds.zeday.domain.dto.user.AddUserDto;
import com.ayds.zeday.domain.dto.user.UpdateUserDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.UserEntity;

public class UserParamsResolver extends RandomParamsResolver {

    public UserParamsResolver() {
        super(List.of(UserDto.class, AddUserDto.class, UpdateUserDto.class, UserEntity.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        if (type == UserDto.class) {
            return getUserDto();
        }
        if (type == AddUserDto.class) {
            return getAddUserDto();
        }
        if (type == UpdateUserDto.class) {
            return getUpdateUserDto();
        }
        if (type == UserEntity.class) {
            return getUserEntity();
        }
        return null;
    }

    private UserDto getUserDto() {
        UserDto user = mock(UserDto.class);

        given(user.getId()).willReturn(random.nextPositiveLong());
        given(user.getName()).willReturn(random.nextString());
        given(user.getLastname()).willReturn(random.nextString());
        given(user.getEmail()).willReturn(random.nextString());
        given(user.getNit()).willReturn(random.nextString());
        given(user.getCui()).willReturn(random.nextString());
        given(user.getPhone()).willReturn(random.nextString());
        given(user.getActiveMfa()).willReturn(random.nextBoolean());
        given(user.getCreatedAt()).willReturn(random.nextInstant());
        given(user.getUpdatedAt()).willReturn(random.nextInstant());
        given(user.getPermissions()).willReturn(random.nextStrings());

        return user;
    }

    private AddUserDto getAddUserDto() {
        return AddUserDto.builder()
                .name(random.nextString())
                .lastname(random.nextString())
                .email(random.nextString())
                .password(random.nextString())
                .nit(random.nextString())
                .cui(random.nextString())
                .phone(random.nextString())
                .build();
    }

    private UpdateUserDto getUpdateUserDto() {
        return UpdateUserDto.builder()
                .phone(Optional.of(random.nextString()))
                .currentPassword(Optional.of(random.nextString()))
                .newPassword(Optional.of(random.nextString()))
                .build();
    }

    private UserEntity getUserEntity() {
        return UserEntity.builder()
                .id(random.nextPositiveLong())
                .email(random.nextString())
                .password(random.nextString())
                .mfaSecret(random.nextString())
                .name(random.nextString())
                .lastname(random.nextString())
                .nit(random.nextString())
                .cui(random.nextString())
                .phone(random.nextString())
                .activeMfa(random.nextBoolean())
                .business(mock(BusinessEntity.class))
                .roles(List.of())
                .build();
    }
}

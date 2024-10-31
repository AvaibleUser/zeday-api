package com.ayds.zeday.util.paramresolver;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import com.ayds.zeday.domain.dto.user.AddUserDto;
import com.ayds.zeday.domain.dto.user.MfaUserDto;
import com.ayds.zeday.domain.dto.user.MfaUserDto.MfaUserDtoImpl;
import com.ayds.zeday.domain.dto.user.UpdateUserDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.dto.user.UserDto.UserDtoImpl;
import com.ayds.zeday.domain.dto.user.UserRoleDto;
import com.ayds.zeday.domain.dto.user.UserRoleDto.UserRoleDtoImpl;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.UserEntity;

public class UserParamsResolver extends RandomParamsResolver {

    public UserParamsResolver() {
        super(List.of(UserDto.class, UserDtoImpl.class, UserRoleDto.class, MfaUserDto.class, MfaUserDtoImpl.class,
                AddUserDto.class, UpdateUserDto.class, UserEntity.class),
                List.of(UserDto.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        if (type == UserDto.class) {
            return getUserDto();
        }
        if (type == UserDtoImpl.class) {
            return getUserDto();
        }
        if (type == UserRoleDto.class) {
            return getUserRoleDto();
        }
        if (type == MfaUserDto.class) {
            return getMfaUserDto();
        }
        if (type == MfaUserDtoImpl.class) {
            return getMfaUserDto();
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
        if (type == List.class) {
            Type genericType = parameter.getParameterizedType();
            if (genericType instanceof ParameterizedType genType) {
                Type genClass = Arrays.stream(genType.getActualTypeArguments()).findFirst().get();
                if (genClass == UserDto.class) {
                    return random.nextObjects(this::getUserDto, toList());
                }
            }
        }
        return null;
    }

    private UserDto getUserDto() {
        return UserDtoImpl.builder()
                .id(random.nextPositiveLong())
                .email(random.nextString())
                .name(random.nextString())
                .lastname(random.nextString())
                .nit(random.nextString())
                .cui(random.nextString())
                .phone(random.nextString())
                .activeMfa(random.nextBoolean())
                .build();
    }

    private UserRoleDto getUserRoleDto() {
        return UserRoleDtoImpl.builder()
                .id(random.nextPositiveLong())
                .name(random.nextString())
                .lastname(random.nextString())
                .email(random.nextString())
                .nit(random.nextString())
                .cui(random.nextString())
                .phone(random.nextString())
                .activeMfa(random.nextBoolean())
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .permissions(random.nextStrings())
                .build();
    }

    private MfaUserDto getMfaUserDto() {
        return MfaUserDtoImpl.builder()
                .name(random.nextString())
                .mfaSecret(random.nextString())
                .activeMfa(random.nextBoolean())
                .build();
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
                .roles(Set.of())
                .build();
    }
}

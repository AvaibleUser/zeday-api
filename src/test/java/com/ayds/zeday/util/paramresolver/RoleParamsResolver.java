package com.ayds.zeday.util.paramresolver;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import com.ayds.zeday.domain.dto.role.AddRoleDto;
import com.ayds.zeday.domain.dto.role.GeneralRoleDto;
import com.ayds.zeday.domain.dto.role.RoleDto;
import com.ayds.zeday.domain.dto.role.RoleDto.RoleDtoImpl;
import com.ayds.zeday.domain.dto.role.UpdateRoleDto;
import com.ayds.zeday.domain.dto.user.UserRoleDto;
import com.ayds.zeday.domain.dto.user.UserRoleDto.UserRoleDtoImpl;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.PermissionEntity;
import com.ayds.zeday.domain.entity.RoleEntity;

public class RoleParamsResolver extends RandomParamsResolver {

    public RoleParamsResolver() {
        super(List.of(RoleDto.class, AddRoleDto.class, UpdateRoleDto.class, RoleEntity.class), List.of(RoleDto.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        if (type == RoleDto.class) {
            return getRoleDto();
        }
        if (type == UserRoleDto.class) {
            return getUserRoleDto();
        }
        if (type == AddRoleDto.class) {
            return getAddRoleDto();
        }
        if (type == UpdateRoleDto.class) {
            return getUpdateRoleDto();
        }
        if (type == RoleEntity.class) {
            return getRoleEntity();
        }
        if (type == List.class) {
            Type genericType = parameter.getParameterizedType();
            if (genericType instanceof ParameterizedType genType) {
                Type genClass = Arrays.stream(genType.getActualTypeArguments()).findFirst().get();
                if (genClass == RoleDto.class) {
                    return random.nextObjects(this::getRoleDto, toList());
                }
            }
        }
        return null;
    }

    private RoleDto getRoleDto() {
        return RoleDtoImpl.builder()
                .id(random.nextPositiveLong())
                .name(random.nextString())
                .description(random.nextString())
                .multiuser(random.nextBoolean())
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .build();
    }

    private UserRoleDto getUserRoleDto() {
        return UserRoleDtoImpl.builder()
                .id(random.nextPositiveLong())
                .email(random.nextString())
                .lastname(random.nextString())
                .nit(random.nextString())
                .cui(random.nextString())
                .phone(random.nextString())
                .activeMfa(random.nextBoolean())
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .permissions(random.nextStrings())
                .roles(random.nextMocks(GeneralRoleDto.class, toSet()))
                .build();
    }

    private AddRoleDto getAddRoleDto() {
        return AddRoleDto.builder()
                .name(random.nextString())
                .description(random.nextString())
                .multiuser(Optional.of(random.nextBoolean()))
                .build();
    }

    private UpdateRoleDto getUpdateRoleDto() {
        return UpdateRoleDto.builder()
                .description(Optional.of(random.nextString()))
                .multiuser(Optional.of(random.nextBoolean()))
                .build();
    }

    private RoleEntity getRoleEntity() {
        return RoleEntity.builder()
                .id(random.nextPositiveLong())
                .name(random.nextString())
                .description(random.nextString())
                .multiuser(random.nextBoolean())
                .permissions(random.nextMocks(PermissionEntity.class, toSet()))
                .business(mock(BusinessEntity.class))
                .build();
    }
}

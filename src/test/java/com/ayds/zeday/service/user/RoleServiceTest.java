package com.ayds.zeday.service.user;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ayds.zeday.domain.dto.role.AddRoleDto;
import com.ayds.zeday.domain.dto.role.RoleDto;
import com.ayds.zeday.domain.dto.role.UpdateRoleDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.RoleEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.PermissionRepository;
import com.ayds.zeday.repository.RoleRepository;
import com.ayds.zeday.util.annotation.ZedayTest;
import com.ayds.zeday.util.paramresolver.BusinessParamsResolver;
import com.ayds.zeday.util.paramresolver.RoleParamsResolver;

@ZedayTest
@ExtendWith({ RoleParamsResolver.class, BusinessParamsResolver.class })
public class RoleServiceTest {

    @Captor
    private ArgumentCaptor<RoleEntity> roleCaptor;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private BusinessRepository businessRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    public void canFindAllRolesForBusiness(long businessId, List<RoleDto> expectedRoles) {
        given(roleRepository.findAllByPermissionsScheduleIdAndBusinessIdOrBusinessIdAndPermissionsScheduleId(null, null,
                businessId, null, RoleDto.class))
                .willReturn(expectedRoles.stream().toList());

        List<RoleDto> actualRoles = roleService.findAllRolesForBusiness(businessId);

        then(actualRoles).isEqualTo(expectedRoles);
    }

    @Test
    public void canAddBusinessRole(long businessId, AddRoleDto role, BusinessEntity business) {
        given(businessRepository.findById(businessId)).willReturn(Optional.of(business));
        given(roleRepository.existsByNameAndBusinessId(role.name(), businessId)).willReturn(false);

        roleService.addBusinessRole(businessId, role);

        verify(roleRepository).save(roleCaptor.capture());
        then(roleCaptor.getValue()).extracting(RoleEntity::getName).isEqualTo(role.name());
        then(roleCaptor.getValue()).extracting(RoleEntity::getDescription).isEqualTo(role.description());
        then(roleCaptor.getValue()).extracting(RoleEntity::getMultiuser).isEqualTo(role.multiuser().get());
    }

    @Test
    public void canBlockAddBusinessRoleWithBusinessNotFound(long businessId, AddRoleDto role) {
        given(businessRepository.findById(businessId)).willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> roleService.addBusinessRole(businessId, role));
    }

    @Test
    public void canBlockAddBusinessRoleWithDuplicateName(long businessId, AddRoleDto role, BusinessEntity business) {
        given(businessRepository.findById(businessId)).willReturn(Optional.of(business));
        given(roleRepository.existsByNameAndBusinessId(role.name(), businessId)).willReturn(true);

        assertThrows(RequestConflictException.class, () -> roleService.addBusinessRole(businessId, role));
    }

    @Test
    public void canUpdateBusinessRole(long businessId, long roleId, UpdateRoleDto role, RoleEntity roleEntity) {
        given(roleRepository.findByIdAndBusinessId(roleId, businessId, RoleEntity.class))
                .willReturn(Optional.of(roleEntity));

        roleService.updateBusinessRole(businessId, roleId, role);

        verify(roleRepository).save(roleCaptor.capture());
        then(roleCaptor.getValue()).extracting(RoleEntity::getDescription).isEqualTo(role.description().get());
        then(roleCaptor.getValue()).extracting(RoleEntity::getMultiuser).isEqualTo(role.multiuser().get());
    }

    @Test
    public void canBlockUpdateBusinessRoleWithRoleNotFound(long businessId, long roleId, UpdateRoleDto role,
            RoleEntity roleEntity) {
        given(roleRepository.findByIdAndBusinessId(roleId, businessId, RoleEntity.class))
                .willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> roleService.updateBusinessRole(businessId, roleId, role));
    }
}

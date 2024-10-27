package com.ayds.zeday.service.user;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.ayds.zeday.domain.dto.permission.PermissionDto;
import com.ayds.zeday.domain.dto.role.AddRoleDto;
import com.ayds.zeday.domain.dto.role.RoleDto;
import com.ayds.zeday.domain.dto.role.UpdateRoleDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.PermissionEntity;
import com.ayds.zeday.domain.entity.RoleEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.PermissionRepository;
import com.ayds.zeday.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final BusinessRepository businessRepository;

    public List<RoleDto> findAllRolesForBusiness(long businessId) {
        return roleRepository.findAllByBusinessIdOrBusinessId(null, businessId, RoleDto.class)
                .stream()
                .filter(role -> role.permissions()
                        .stream()
                        .map(PermissionDto::schedule)
                        .allMatch(Objects::isNull))
                .toList();
    }

    public void addBusinessRole(long businessId, AddRoleDto role) {
        BusinessEntity business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la compañia actual"));

        if (roleRepository.existsByNameAndBusinessId(role.name(), businessId)) {
            throw new RequestConflictException("El nombre del rol ya esta en uso");
        }

        RoleEntity newRole = RoleEntity.builder()
                .name(role.name())
                .description(role.description())
                .multiuser(role.multiuser().orElse(false))
                .business(business)
                .build();

        roleRepository.save(newRole);
    }

    public void updateBusinessRole(long businessId, long roleId, UpdateRoleDto role) {
        RoleEntity dbRole = roleRepository.findByIdAndBusinessId(roleId, businessId, RoleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("El rol no se encontro"));

        role.description().ifPresent(dbRole::setDescription);
        role.multiuser().ifPresent(dbRole::setMultiuser);

        roleRepository.save(dbRole);
    }

    public void toggleBusinessRolePermissions(long businessId, long roleId, List<Long> permissionIds) {
        RoleEntity role = roleRepository.findByIdAndBusinessId(roleId, businessId, RoleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el rol"));

        role.getPermissions().removeIf(permission -> !permissionIds.contains(permission.getId()));

        List<Long> actualPermissionIds = role.getPermissions()
                .stream()
                .map(PermissionEntity::getId)
                .toList();

        permissionIds.removeAll(actualPermissionIds);

        List<PermissionEntity> permissionsToAdd = permissionRepository.findAllByIdInAndBusinessId(permissionIds,
                businessId);

        if (!permissionIds.containsAll(permissionsToAdd.stream().map(PermissionEntity::getId).toList())) {
            throw new ValueNotFoundException("No se pudieron encontrar todos los roles");
        }

        role.getPermissions().addAll(permissionsToAdd);

        roleRepository.save(role);
    }
}

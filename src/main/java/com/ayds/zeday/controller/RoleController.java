package com.ayds.zeday.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.domain.dto.role.AddRoleDto;
import com.ayds.zeday.domain.dto.role.RoleDto;
import com.ayds.zeday.domain.dto.role.UpdateRoleDto;
import com.ayds.zeday.service.user.RoleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleDto>> getBusinessRoles(@RequestHeader("CompanyId") @Positive long businessId) {
        List<RoleDto> roles = roleService.findAllRolesForBusiness(businessId);

        return ResponseEntity.ok(roles);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public void createBusinessRole(@RequestHeader("CompanyId") @Positive long businessId,
            @RequestBody @Valid AddRoleDto role) {
        roleService.addBusinessRole(businessId, role);
    }

    @PutMapping("/{roleId}")
    @ResponseStatus(NO_CONTENT)
    public void updateBusinessRole(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long roleId, @RequestBody @Valid UpdateRoleDto role) {
        roleService.updateBusinessRole(businessId, roleId, role);
    }

    @PutMapping("/{roleId}/permissions/{permissionIds}")
    @ResponseStatus(NO_CONTENT)
    public void toggleRolePermissions(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long roleId, @PathVariable @NotEmpty List<@Positive Long> permissionIds) {
        roleService.toggleBusinessRolePermissions(businessId, roleId, permissionIds);
    }
}

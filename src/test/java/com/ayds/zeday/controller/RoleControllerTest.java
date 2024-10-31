package com.ayds.zeday.controller;

import static java.util.stream.Collectors.joining;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ayds.zeday.domain.dto.role.AddRoleDto;
import com.ayds.zeday.domain.dto.role.RoleDto;
import com.ayds.zeday.domain.dto.role.UpdateRoleDto;
import com.ayds.zeday.service.user.RoleService;
import com.ayds.zeday.util.annotation.ZedayWebTest;
import com.ayds.zeday.util.paramresolver.RoleParamsResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

@ZedayWebTest
@ExtendWith({ RoleParamsResolver.class })
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RoleService roleService;

    @Test
    public void canGetBusinessRoles(long businessId, List<RoleDto> expectedRoles) throws Exception {
        given(roleService.findAllRolesForBusiness(businessId)).willReturn(expectedRoles);

        ResultActions actualResults = mockMvc.perform(
                get("/api/roles")
                        .header("CompanyId", businessId));

        actualResults.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedRoles)));
    }

    @Test
    public void canCreateBusinessRole(long businessId, AddRoleDto role) throws Exception {
        willDoNothing().given(roleService).addBusinessRole(businessId, role);

        ResultActions actualResults = mockMvc.perform(
                post("/api/roles")
                        .header("CompanyId", businessId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(role)));

        actualResults.andExpect(status().isCreated());
    }

    @Test
    public void canUpdateBusinessRole(long businessId, long roleId, UpdateRoleDto role) throws Exception {
        willDoNothing().given(roleService).updateBusinessRole(businessId, roleId, role);

        ResultActions actualResults = mockMvc.perform(
                put("/api/roles/{roleId}", roleId)
                        .header("CompanyId", businessId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(role)));
        ;

        actualResults.andExpect(status().isNoContent());
    }

    @Test
    public void canToggleBusinessRolePermissions(long businessId, long roleId, List<Long> permissionIds)
            throws Exception {
        willDoNothing().given(roleService).toggleBusinessRolePermissions(businessId, roleId, permissionIds);

        ResultActions actualResults = mockMvc.perform(
                put("/api/roles/{roleId}/permissions/{permissionIds}", roleId,
                        permissionIds.stream()
                                .map(String::valueOf)
                                .collect(joining(",")))
                        .header("CompanyId", businessId));

        actualResults.andExpect(status().isNoContent());
    }
}

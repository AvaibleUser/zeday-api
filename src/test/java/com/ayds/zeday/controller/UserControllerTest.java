package com.ayds.zeday.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ayds.zeday.domain.dto.TokenDto;
import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.dto.user.ConfirmMfaDto;
import com.ayds.zeday.domain.dto.user.MfaSecretDto;
import com.ayds.zeday.domain.dto.user.MfaUserDto.MfaUserDtoImpl;
import com.ayds.zeday.domain.dto.user.UpdateUserDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.dto.user.UserRoleDto;
import com.ayds.zeday.repository.UserRepository;
import com.ayds.zeday.service.business.BusinessService;
import com.ayds.zeday.service.user.MfaAuthService;
import com.ayds.zeday.service.user.UserService;
import com.ayds.zeday.service.util.TokenService;
import com.ayds.zeday.util.annotation.ZedayWebTest;
import com.ayds.zeday.util.paramresolver.BusinessParamsResolver;
import com.ayds.zeday.util.paramresolver.UserParamsResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

@ZedayWebTest
@ExtendWith({ UserParamsResolver.class, BusinessParamsResolver.class })
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MfaAuthService mfaAuthService;

    @MockBean
    private UserService userService;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private TokenService tokenService;

    @Test
    public void canGetMe(UserDto user) throws Exception {
        given(userRepository.findUserDtoById(user.getId())).willReturn(Optional.of(user));

        ResultActions actualResult = mockMvc.perform(
                get("/api/users/me")
                        .header("CompanyId", 1L)
                        .with(jwt().jwt(builder -> builder.subject(String.valueOf(user.getId())))));

        actualResult.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user)));
    }

    @Test
    public void canGetUser(UserRoleDto user) throws Exception {
        given(userService.findUserByEmailWithRoles(1L, user.getEmail())).willReturn(Optional.of(user));

        ResultActions actualResult = mockMvc.perform(
                get("/api/users/{email}", user.getEmail())
                        .header("CompanyId", 1L));

        actualResult.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(user)));
    }

    @Test
    @Disabled
    public void canUpdateMe(long businessId, String token, UserDto user, UpdateUserDto update) throws Exception {
        TokenDto expectedToken = new TokenDto(token, user);

        given(userRepository.findUserDtoById(user.getId())).willReturn(Optional.of(user));
        given(tokenService.generateToken(user.getId(), user.getPermissions())).willReturn(token);
        willDoNothing().given(userService).changeUserInfo(user.getId(), update);

        ResultActions actualResult = mockMvc.perform(
                put("/api/users/me")
                        .header("CompanyId", businessId)
                        .with(jwt().jwt(builder -> builder.subject(String.valueOf(user.getId()))))
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(update)));

        actualResult.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expectedToken)));
    }

    @Test
    public void canAddUserRole(long businessId, long userId, long roleId) throws Exception {
        willDoNothing().given(userService).addUserRole(businessId, userId, roleId);

        ResultActions actualResult = mockMvc.perform(
                post("/api/users/{userId}/roles/{roleIds}", userId, roleId)
                        .header("CompanyId", businessId));

        actualResult.andExpect(status().isNoContent());
    }

    @Test
    public void canToggleUserRoles(long businessId, long userId, List<Long> roleIds) throws Exception {
        willDoNothing().given(userService).toggleUserRoles(businessId, userId, roleIds);

        ResultActions actualResult = mockMvc.perform(
                put("/api/users/{userId}/roles/{roleId}", userId,
                        roleIds.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(",")))
                        .header("CompanyId", businessId));

        actualResult.andExpect(status().isNoContent());
    }

    @Test
    @Disabled
    public void canGenerateMultiFactorAuthentication(long businessId, MfaUserDtoImpl user,
            BusinessDto business, String qrUrl) throws Exception {
        user.setActiveMfa(false);

        given(userRepository.findMfaUserDtoById(user.getId())).willReturn(Optional.of(user));
        given(businessService.findBusiness(businessId)).willReturn(Optional.of(business));
        given(mfaAuthService.generateGoogleAuthQrUrlByMfaSecret(business.name(), user.getName(), user.getMfaSecret()))
                .willReturn(qrUrl);

        ResultActions actualResult = mockMvc.perform(
                get("/api/users/multifactor-authentication")
                        .header("CompanyId", businessId)
                        .with(jwt().jwt(builder -> builder.subject(String.valueOf(user.getId())))));

        actualResult.andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new MfaSecretDto(qrUrl, user.getMfaSecret()))));
    }

    @Test
    @Disabled
    public void canBlockMultiFactorAuthenticationByMfaActive(long businessId, MfaUserDtoImpl user,
            BusinessDto business) throws Exception {
        user.setActiveMfa(true);

        given(userRepository.findMfaUserDtoById(user.getId())).willReturn(Optional.of(user));
        given(businessService.findBusiness(businessId)).willReturn(Optional.of(business));

        ResultActions actualResult = mockMvc.perform(
                get("/api/users/multifactor-authentication")
                        .header("CompanyId", businessId)
                        .with(jwt().jwt(builder -> builder.subject(String.valueOf(user.getId())))));

        actualResult.andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    public void canBlockMultiFactorAuthenticationByBusinessNotFound(long businessId, MfaUserDtoImpl user)
            throws Exception {
        user.setActiveMfa(false);

        given(userRepository.findMfaUserDtoById(user.getId())).willReturn(Optional.of(user));
        given(businessService.findBusiness(businessId)).willReturn(Optional.empty());

        ResultActions actualResult = mockMvc.perform(
                get("/api/users/multifactor-authentication")
                        .header("CompanyId", businessId)
                        .with(jwt().jwt(builder -> builder.subject(String.valueOf(user.getId())))));

        actualResult.andExpect(status().isNotFound());
    }

    @Test
    @Disabled
    public void canAddMultiFactorAuthentication(long businessId, MfaUserDtoImpl user, int code)
            throws Exception {
        user.setActiveMfa(false);

        given(userRepository.findMfaUserDtoById(user.getId())).willReturn(Optional.of(user));
        given(businessService.findBusiness(businessId)).willReturn(Optional.empty());
        given(mfaAuthService.authencateUserWithMfaAuth(user.getMfaSecret(), code)).willReturn(false);

        ResultActions actualResult = mockMvc.perform(
                patch("/api/users/multifactor-authentication")
                        .header("CompanyId", businessId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(new ConfirmMfaDto(String.valueOf(code))))
                        .with(jwt().jwt(builder -> builder.subject(String.valueOf(user.getId())))));

        actualResult.andExpect(status().isNoContent());
    }

    @Test
    @Disabled
    public void canBlockAddMultiFactorAuthenticationByIncorrectCode(long businessId, MfaUserDtoImpl user,
            int code) throws Exception {
        user.setActiveMfa(true);

        given(userRepository.findMfaUserDtoById(user.getId())).willReturn(Optional.of(user));
        given(businessService.findBusiness(businessId)).willReturn(Optional.empty());
        given(mfaAuthService.authencateUserWithMfaAuth(user.getMfaSecret(), code)).willReturn(false);

        ResultActions actualResult = mockMvc.perform(
                patch("/api/users/multifactor-authentication")
                        .header("CompanyId", businessId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(new ConfirmMfaDto(String.valueOf(code))))
                        .with(jwt().jwt(builder -> builder.subject(String.valueOf(user.getId())))));

        actualResult.andExpect(status().isBadRequest());
    }
}

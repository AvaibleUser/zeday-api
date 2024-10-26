package com.ayds.zeday.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.config.annotation.CurrentUserDto;
import com.ayds.zeday.domain.dto.TokenDto;
import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.dto.user.MfaSecretDto;
import com.ayds.zeday.domain.dto.user.MfaUserDto;
import com.ayds.zeday.domain.dto.user.UpdateUserDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.exception.BadRequestException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.service.business.BusinessService;
import com.ayds.zeday.service.user.MfaAuthService;
import com.ayds.zeday.service.user.UserService;
import com.ayds.zeday.service.util.TokenService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BusinessService businessService;
    private final MfaAuthService mfaAuthService;
    private final TokenService tokenService;

    private TokenDto toTokenDto(UserDto user) {
        String token = tokenService.generateToken(user.getId(), user.getPermissions());
        return new TokenDto(token, user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(@CurrentUserDto UserDto me) {
        return ResponseEntity.ok(me);
    }

    @PutMapping
    public ResponseEntity<TokenDto> updateMe(@CurrentUserDto UserDto me, @RequestBody @Valid UpdateUserDto user) {
        userService.changeUserInfo(me.getId(), user);

        return ResponseEntity.ok(toTokenDto(me));
    }

    @PutMapping("/{userId}/roles/{roleIds}")
    @ResponseStatus(NO_CONTENT)
    public void addRolesToUser(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long userId, @PathVariable @NotEmpty List<@Positive Long> roleIds) {
        userService.toggleUserRoles(businessId, userId, roleIds);
    }

    @GetMapping("/multifactor-authentication")
    public ResponseEntity<MfaSecretDto> generateMultiFactorAuthentication(
            @RequestHeader("CompanyId") @Positive long businessId, @CurrentUserDto(MfaUserDto.class) MfaUserDto me) {
        if (me.getActiveMfa()) {
            throw new BadRequestException("El usuario ya tiene activada la autenticacion por dos factores");
        }
        BusinessDto business = businessService.findBusiness(businessId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la compañia"));

        String qrUrl = mfaAuthService.generateGoogleAuthQrUrlByMfaSecret(business.name(), me.getName(),
                me.getMfaSecret());

        return ResponseEntity.ok(new MfaSecretDto(qrUrl, me.getMfaSecret()));
    }
}
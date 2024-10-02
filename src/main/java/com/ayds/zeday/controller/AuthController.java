package com.ayds.zeday.controller;

import static java.util.function.Predicate.not;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.domain.dto.TokenDto;
import com.ayds.zeday.domain.dto.user.AddUserDto;
import com.ayds.zeday.domain.dto.user.AuthUserDto;
import com.ayds.zeday.domain.dto.user.ConfirmUserDto;
import com.ayds.zeday.domain.dto.user.MfaUserDto;
import com.ayds.zeday.domain.dto.user.RecoverUserDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.exception.BadRequestException;
import com.ayds.zeday.domain.exception.FailedAuthenticateException;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.service.user.AuthConfirmationService;
import com.ayds.zeday.service.user.MfaAuthService;
import com.ayds.zeday.service.user.UserService;
import com.ayds.zeday.service.util.EmailService;
import com.ayds.zeday.service.util.TemplateRendererService;
import com.ayds.zeday.service.util.TokenService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;
    private final MfaAuthService mfaAuthService;
    private final AuthConfirmationService authConfirmationService;
    private final EmailService emailService;
    private final TemplateRendererService templateRendererService;
    private final AuthenticationManager authenticationManager;

    private TokenDto toTokenDto(UserDto user) {
        String token = tokenService.generateToken(user.getId(), user.getPermissions());
        return new TokenDto(token, user);
    }

    @PostMapping("/sign-up")
    @ResponseStatus(CREATED)
    public void signUp(@RequestBody @Valid AddUserDto user) {
        String mfaSecretKey = mfaAuthService.generateUserMfaSecretKey();
        userService.registerUser(user, mfaSecretKey);

        String code = authConfirmationService.generateEmailConfirmationCode(user.email());

        Map<String, Object> templateVariables = Map.of("code", code.toCharArray(), "user", user);

        String confirmationHtml = templateRendererService.renderTemplate("sign-up-confirmation", templateVariables);

        try {
            emailService.sendHtmlEmail("zeday", user.email(),
                    "Confirmacion de usuario en zeday", confirmationHtml);
        } catch (MessagingException e) {
            throw new RequestConflictException("No se pudo enviar el correo de confirmacion");
        }
    }

    @PutMapping("/sign-up")
    public ResponseEntity<TokenDto> confirmSignUp(@RequestBody @Valid ConfirmUserDto user) {
        boolean confirmed = authConfirmationService.confirmUserEmailCode(user.email(), user.code());

        if (!confirmed) {
            throw new FailedAuthenticateException("No se pudo confirmar la cuenta");
        }

        TokenDto token = userService.findUserByEmail(user.email())
                .map(this::toTokenDto)
                .orElseThrow(() -> new InsufficientAuthenticationException("No se encontro el registro del usuario"));

        return ResponseEntity.ok(token);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid AuthUserDto user) {
        var authenticableUser = new UsernamePasswordAuthenticationToken(user.email(), user.password());
        authenticationManager.authenticate(authenticableUser);

        return userService.findUserByEmail(user.email())
                .filter(not(UserDto::getActiveMfa))
                .map(this::toTokenDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.accepted().build());
    }

    @PostMapping("/sign-in/2fa")
    public ResponseEntity<TokenDto> signInMfa(@RequestBody @Valid ConfirmUserDto user) {
        MfaUserDto user2fa = userService.findMfaUserByEmail(user.email())
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el registro del usuario"));

        if (!user2fa.getActiveMfa()) {
            throw new BadRequestException("El usuario debe de tener activada la autenticacion por dos factores");
        }
        if (!mfaAuthService.authencateUserWithMfaAuth(user2fa.getMfaSecret(), Integer.parseInt(user.code()))) {
            throw new InsufficientAuthenticationException("La autenticacion en dos factores fallo");
        }

        String token = tokenService.generateToken(user2fa.getId(), user2fa.getPermissions());

        return ResponseEntity.ok(new TokenDto(token, null));
    }

    @PostMapping("/recover-password")
    @ResponseStatus(ACCEPTED)
    public void recoverPassword(@RequestBody @Valid RecoverUserDto user) {
        UserDto dbUser = userService.findUserByEmail(user.email())
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el registro del usuario"));

        String code = authConfirmationService.generateEmailConfirmationCode(dbUser.getEmail());

        Map<String, Object> templateVariables = Map.of("code", code.toCharArray(), "user", dbUser);

        String confirmationHtml = templateRendererService.renderTemplate("recover-password", templateVariables);

        try {
            emailService.sendHtmlEmail("zeday", dbUser.getEmail(), "Recuperacion de contraseña en zeday",
                    confirmationHtml);
        } catch (MessagingException e) {
            throw new RequestConflictException("No se pudo enviar el correo para la recuperacion de contraseña");
        }
    }

    @PutMapping("/recover-password")
    public ResponseEntity<TokenDto> confirmRecoverPassword(@RequestBody @Valid ConfirmUserDto user) {
        boolean confirmed = authConfirmationService.confirmUserEmailCode(user.email(), user.code());

        if (!confirmed) {
            throw new RequestConflictException("No se logro confirmar el cambio de contraseña");
        }

        return userService.findUserByEmail(user.email())
                .map(this::toTokenDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el registro del usuario"));
    }
}

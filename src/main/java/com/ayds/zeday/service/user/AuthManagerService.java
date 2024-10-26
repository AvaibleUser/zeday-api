package com.ayds.zeday.service.user;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;

import java.util.concurrent.ConcurrentMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.entity.UserEntity;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthManagerService implements AuthenticationManager {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ConcurrentMap<Long, ConcurrentMap<String, String>> signUpConfirmationCodes;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authUser) throws AuthenticationException {
        String email = authUser.getPrincipal().toString();
        String password = authUser.getCredentials().toString();
        long businessId = (Long) authUser.getDetails();

        if (!signUpConfirmationCodes.containsKey(businessId)) {
            throw new ValueNotFoundException("No se pudo encontrar la compañia");
        }
        if (signUpConfirmationCodes.get(businessId).containsKey(email)) {
            throw new InsufficientAuthenticationException("La cuenta aun no se ha confirmado");
        }

        UserEntity user = userRepository.findByEmailAndBusinessId(email, businessId, UserEntity.class)
                .filter(dbUser -> encoder.matches(password, dbUser.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("El email o la contraseña es incorrecta"));

        return authenticated(email, password, user.getAuthorities());
    }
}

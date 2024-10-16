package com.ayds.zeday.service.user;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthConfirmationService {

    private final GoogleAuthenticator googleAuth;
    private final ConcurrentMap<Long, ConcurrentMap<String, String>> emailConfirmationCodes;

    public String generateEmailConfirmationCode(long businessId, String email) {
        if (!emailConfirmationCodes.containsKey(businessId)) {
            throw new ValueNotFoundException("No se pudo encontrar la compañia");
        }
        GoogleAuthenticatorKey credentials = googleAuth.createCredentials();
        String code = String.format("%06d", googleAuth.getTotpPassword(credentials.getKey()));
        emailConfirmationCodes.get(businessId).put(email, code);
        return code;
    }

    public boolean confirmUserEmailCode(long businessId, String email, String code) {
        if (!emailConfirmationCodes.containsKey(businessId)) {
            throw new ValueNotFoundException("No se pudo encontrar la compañia");
        }
        return emailConfirmationCodes.get(businessId).remove(email, code);
    }

    public boolean existsUserEmailCode(long businessId, String email, String code) {
        if (!emailConfirmationCodes.containsKey(businessId)) {
            throw new ValueNotFoundException("No se pudo encontrar la compañia");
        }
        return Objects.equals(emailConfirmationCodes.get(businessId).get(email), code);
    }
}

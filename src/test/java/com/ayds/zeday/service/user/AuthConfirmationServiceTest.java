package com.ayds.zeday.service.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.util.annotation.ZedayTest;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@ZedayTest
public class AuthConfirmationServiceTest {

    @Spy
    private GoogleAuthenticator googleAuth = new GoogleAuthenticator();

    @Spy
    private GoogleAuthenticatorKey googleAuthKey = googleAuth.createCredentials();

    @Spy
    private ConcurrentMap<String, String> companyCodes = new ConcurrentHashMap<>();

    @Spy
    private ConcurrentMap<Long, ConcurrentMap<String, String>> emailConfirmationCodes = new ConcurrentHashMap<>();

    @InjectMocks
    private AuthConfirmationService authConfirmationService;

    @Test
    @Disabled
    public void canGenerateEmailConfirmationCode(long businessId, String email, String key, int totp) {
        willReturn(googleAuthKey).given(googleAuth).createCredentials();
        willReturn(key).given(googleAuthKey).getKey();
        willReturn(totp).given(googleAuth).getTotpPassword(key);
        willReturn(true).given(emailConfirmationCodes).containsKey(businessId);
        willReturn(companyCodes).given(emailConfirmationCodes).get(businessId);

        authConfirmationService.generateEmailConfirmationCode(businessId, email);

        verify(googleAuth).getTotpPassword(key);
        verify(emailConfirmationCodes).containsKey(businessId);
        verify(emailConfirmationCodes).get(businessId);
        verify(companyCodes).put(email, String.format("%06d", totp));
    }

    @Test
    @Disabled
    public void canConfirmUserEmailCode(long businessId, String email, String code) {
        companyCodes.put(email, code);

        willReturn(true).given(emailConfirmationCodes).containsKey(businessId);
        willReturn(companyCodes).given(emailConfirmationCodes).get(businessId);

        authConfirmationService.confirmUserEmailCode(businessId, email, code);

        verify(emailConfirmationCodes).containsKey(businessId);
        verify(emailConfirmationCodes).get(businessId);
        verify(companyCodes).remove(email, code);
    }

    @Test
    @Disabled
    public void canConfirmUserEmailCodeWithCodeNotFound(long businessId, String email, String code) {
        willReturn(true).given(emailConfirmationCodes).containsKey(businessId);
        willReturn(companyCodes).given(emailConfirmationCodes).get(businessId);

        authConfirmationService.existsUserEmailCode(businessId, email, code);

        verify(emailConfirmationCodes).containsKey(businessId);
        verify(emailConfirmationCodes).get(businessId);
        verify(companyCodes).get(email);
    }

    @Test
    public void canBlockGenerateEmailConfirmationCodeWithBusinessNotFound(long businessId, String email) {
        assertThrows(ValueNotFoundException.class,
                () -> authConfirmationService.generateEmailConfirmationCode(businessId, email));
    }

    @Test
    public void canBlockConfirmUserEmailCodeWithCodeNotFound(long businessId, String email, String code) {
        assertThrows(ValueNotFoundException.class,
                () -> authConfirmationService.confirmUserEmailCode(businessId, email, code));
    }

    @Test
    public void canBlockExistsUserEmailCodeWithCodeNotFound(long businessId, String email, String code) {
        assertThrows(ValueNotFoundException.class,
                () -> authConfirmationService.existsUserEmailCode(businessId, email, code));
    }
}

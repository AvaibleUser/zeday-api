package com.ayds.zeday.service.user;

import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MfaAuthService {

    private final GoogleAuthenticator googleAuth;

    public String generateGoogleAuthQrUrlByMfaSecret(String companyName, String userName, String secretKey) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(companyName, userName,
                new GoogleAuthenticatorKey.Builder(secretKey).build());
    }

    public boolean authencateUserWithMfaAuth(String secretKey, int code) {
        return googleAuth.authorize(secretKey, code);
    }

    public String generateUserMfaSecretKey() {
        GoogleAuthenticatorKey credentials = googleAuth.createCredentials();
        return credentials.getKey();
    }
}

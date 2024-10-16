package com.ayds.zeday.config;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.ayds.zeday.property.TokenProperties;
import com.ayds.zeday.util.annotation.ZedayTest;

@ZedayTest
public class AuthConfigTest {

    @Autowired
    private TokenProperties tokenProperties;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private AuthConfig authConfig;

    @Test
    public void canConvertJwtToAuthentication(long expectedId, List<String> expectedAuthorities) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(tokenProperties.expirationTime(), tokenProperties.timeUnit()))
                .subject(String.valueOf(expectedId))
                .claim("auths", expectedAuthorities)
                .build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

        AbstractAuthenticationToken actualAuthentication = authConfig.convertJwtToAuthentication(jwt);

        then(actualAuthentication)
                .extracting(AbstractAuthenticationToken::getName)
                .asString()
                .asFloat()
                .isEqualTo(expectedId);

        then(actualAuthentication)
                .extracting(AbstractAuthenticationToken::getAuthorities)
                .isEqualTo(expectedAuthorities);
    }
}

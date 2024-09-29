package com.ayds.zeday.service.util;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.ayds.zeday.property.TokenProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final TokenProperties tokenProperties;

    private String generateToken(long id, boolean temporal) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(tokenProperties.expirationTime(), tokenProperties.timeUnit()))
                .subject(String.valueOf(id))
                .claim("temporal", temporal)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateToken(long id) {
        return generateToken(id, false);
    }

    public String generateTemporalToken(long id) {
        return generateToken(id, true);
    }
}

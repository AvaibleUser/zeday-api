package com.ayds.zeday.service.util;

import java.time.Instant;
import java.util.Collection;

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

    public <T> String generateToken(long id, Collection<T> authorities) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(tokenProperties.expirationTime(), tokenProperties.timeUnit()))
                .subject(String.valueOf(id))
                .claim("auths", authorities)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}

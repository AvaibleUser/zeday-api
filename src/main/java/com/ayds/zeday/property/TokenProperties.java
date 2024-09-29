package com.ayds.zeday.property;

import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security.token")
public record TokenProperties(
        Long expirationTime,
        ChronoUnit timeUnit) {
}

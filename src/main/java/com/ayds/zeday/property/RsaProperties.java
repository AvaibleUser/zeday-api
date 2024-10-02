package com.ayds.zeday.property;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("security.rsa")
public record RsaProperties(
        RSAPublicKey publicKey,
        RSAPrivateKey privateKey) {
}

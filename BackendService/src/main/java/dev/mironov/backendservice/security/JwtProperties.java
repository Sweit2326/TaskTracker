package dev.mironov.backendservice.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "jwt")
@EnableConfigurationProperties(JwtProperties.class)
public record JwtProperties(
        String secretKey,
        Duration expiration
) {}

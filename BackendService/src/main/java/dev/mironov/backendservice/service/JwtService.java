package dev.mironov.backendservice.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import dev.mironov.backendservice.exception.InvalidJwtTokenException;
import dev.mironov.backendservice.exception.JwtInfrastructureException;
import dev.mironov.backendservice.security.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class JwtService {
    private final JwtProperties props;
    private final byte[] secretBytes;
    private final DefaultJWTProcessor<SecurityContext> jwtProcessor;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.secretBytes = props.secretKey().getBytes(StandardCharsets.UTF_8);
        this.jwtProcessor = new DefaultJWTProcessor<>();

        if (secretBytes.length < 32)
            throw new JwtInfrastructureException("JWT secret must be at least 32 bytes long");

        SecretKey secretKey = new SecretKeySpec(secretBytes, "HMACSHA256");
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretKey).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                JWSAlgorithm.HS256,
                jwkSource
        );

        this.jwtProcessor.setJWSKeySelector(keySelector);
    }

    public String generateToken(String email) {
        try {
            Date now = new Date();
            Date expiration = new Date(now.getTime() + props.expiration().toMillis());

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(email)
                    .issueTime(now)
                    .expirationTime(expiration)
                    .build();
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(new MACSigner(secretBytes));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new JwtInfrastructureException("Failed to sign JWT", e);
        }
    }

    public String extractEmail(String token) {
        try {
            return jwtProcessor.process(token, null).getSubject();
        } catch (ParseException e) {
            throw new InvalidJwtTokenException("Token syntax or structure is malformed");
        } catch (BadJOSEException e) {
            throw new InvalidJwtTokenException("Token verification failed: " + e.getMessage());
        } catch (JOSEException e) {
            throw new JwtInfrastructureException("Internal cryptographic error during token verification", e);
        }
    }
}
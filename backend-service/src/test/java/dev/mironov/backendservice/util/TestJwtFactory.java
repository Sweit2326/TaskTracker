package dev.mironov.backendservice.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dev.mironov.backendservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TestJwtFactory {
    private final JwtService jwtService;
    @Value("${jwt.secret-key}")
    String secretKey;
    @Value("${jwt.expiration}")
    Duration expiration;

    public String generateValidToken(String subject) {
        return jwtService.generateToken(subject);
    }

    public String generateExpiredToken(String subject) {
        return generateToken(
                subject,
                Date.from(Instant.now().minus(2, ChronoUnit.MINUTES)),
                Date.from(Instant.now().minus(1, ChronoUnit.MINUTES)),
                JWSAlgorithm.HS256,
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateInvalidToken() {
        return "static.trash.string.that.is.not.even.a.valid.jwt.token.at.all.1234567890.abcdefg";
    }

    public String generateTokenWithoutSubject() {
        return generateToken(
                null,
                Date.from(Instant.now()),
                Date.from(Instant.now().plus(expiration)),
                JWSAlgorithm.HS256,
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateTokenWithInvalidSign(String subject) {
        return generateToken(
                subject,
                Date.from(Instant.now()),
                Date.from(Instant.now().plus(expiration)),
                JWSAlgorithm.HS512,
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateTokenWithChangedPayload(String subject) {
        return generateToken(
                subject,
                Date.from(Instant.now()),
                Date.from(Instant.now().plus(expiration)),
                JWSAlgorithm.HS256,
                "this-bro-is-legit-signature-trust-me".getBytes(StandardCharsets.UTF_8)
        );
    }

    private String generateToken(String subject, Date issueTime, Date expiration, JWSAlgorithm algorithm, byte[] sign) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(issueTime)
                    .expirationTime(expiration)
                    .build();
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(algorithm), claimsSet);
            signedJWT.sign(new MACSigner(sign));

            return signedJWT.serialize();
        } catch (JOSEException _) {
            return null;
        }
    }
}

package dev.mironov.backendservice.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dev.mironov.backendservice.exception.InvalidJwtTokenException;
import dev.mironov.backendservice.security.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class JwtService {
    private final JwtProperties props;
    private final byte[] secretBytes;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.secretBytes = props.secretKey().getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < 32)
            throw new IllegalStateException("JWT secret must be at least 32 bytes long");
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
            throw new IllegalStateException("Failed to sign JWT", e);
        }
    }

    public String extractEmail(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secretBytes);
            if (!signedJWT.verify(verifier))
                throw new InvalidJwtTokenException("Invalid JWT signature");

            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Date expiration = claimsSet.getExpirationTime();
            if (expiration == null || expiration.before(new Date()))
                throw new InvalidJwtTokenException("JWT token is expired");

            String email = claimsSet.getSubject();
            if (email == null || email.isBlank())
                throw new InvalidJwtTokenException("JWT subject is missing");

            return email;
        } catch (JOSEException | ParseException e) {
            throw new InvalidJwtTokenException("Invalid JWT token", e);
        }
    }
}

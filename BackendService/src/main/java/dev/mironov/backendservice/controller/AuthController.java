package dev.mironov.backendservice.controller;

import dev.mironov.backendservice.record.AccessRequest;
import dev.mironov.backendservice.service.JwtService;
import dev.mironov.backendservice.service.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final RegisterService regService;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody AccessRequest req, HttpServletRequest httpReq, HttpServletResponse httpResp) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        String token = jwtService.generateToken(auth.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION);

        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/user")
    public ResponseEntity<?> register(@Valid @RequestBody AccessRequest req, HttpServletRequest httpReq, HttpServletResponse httpResp) {
        regService.registerUser(req);
        return login(req, httpReq, httpResp);
    }

    // эндпоинт для фронта
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}

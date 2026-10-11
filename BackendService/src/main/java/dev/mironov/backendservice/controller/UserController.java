package dev.mironov.backendservice.controller;

import dev.mironov.backendservice.record.UserResponse;
import dev.mironov.backendservice.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    @GetMapping("/user")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(new UserResponse(userDetails.getId(), userDetails.getUsername()));
    }
}

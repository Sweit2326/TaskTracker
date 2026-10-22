package dev.mironov.backendservice.service;

import dev.mironov.backendservice.exception.UserNotFoundException;
import dev.mironov.backendservice.exception.EmailAlreadyTakenException;
import dev.mironov.backendservice.model.UserModel;
import dev.mironov.backendservice.record.AccessRequest;
import dev.mironov.backendservice.repository.UserRepository;
import dev.mironov.event.EmailSentEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OutboxService outboxService;

    @Transactional
    public void registerUser(AccessRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyTakenException();
        } else {
            userRepository.save(new UserModel(req.email(), passwordEncoder.encode(req.password())));
        }
    }

    public void sendRegisterEmail(String email) {
        UserModel user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Cannot send register message (Email: "+email+")"));
        EmailSentEvent event = new EmailSentEvent(
                user.getId(),
                user.getEmail(),
                "Welcome to TaskTracker, User#"+user.getId(),
                "We are happy that you're registered!",
                Instant.now());
        outboxService.create(event, "user-registered", "RegisterService");
    }
}

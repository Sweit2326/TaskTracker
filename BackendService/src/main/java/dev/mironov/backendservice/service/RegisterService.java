package dev.mironov.backendservice.service;

import dev.mironov.backendservice.exception.EmailAlreadyTakenException;
import dev.mironov.backendservice.model.UserModel;
import dev.mironov.backendservice.record.AccessRequest;
import dev.mironov.backendservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(AccessRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyTakenException();
        } else {
            UserModel user = userRepository.save(new UserModel(req.email(), passwordEncoder.encode(req.password())));
            // TODO: kafka-event to send register email
        }
    }
}

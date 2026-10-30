package dev.mironov.backendservice.util;

import dev.mironov.backendservice.model.UserModel;
import dev.mironov.backendservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestUserFactory {
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserModel createUser() {
        return repository.save(new UserModel(UUID.randomUUID()+"@test.com", encoder.encode("test")));
    }
}

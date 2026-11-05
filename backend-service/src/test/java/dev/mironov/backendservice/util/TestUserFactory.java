package dev.mironov.backendservice.util;

import dev.mironov.backendservice.model.UserModel;
import dev.mironov.backendservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TestUserFactory {
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public static String generateTooLongRequest() {
        return new Random().ints(256, 'a', 'z' + 1)
                .mapToObj(i -> String.valueOf((char) i))
                .collect(Collectors.joining());
    }

    public void createUser(String email, String password) {
        repository.save(new UserModel(email, encoder.encode(password)));
    }

    public UserModel createRandomUser() {
        return repository.save(new UserModel(UUID.randomUUID()+"@mail.com", encoder.encode("password")));
    }
}

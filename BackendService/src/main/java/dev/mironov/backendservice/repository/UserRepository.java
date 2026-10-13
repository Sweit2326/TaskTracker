package dev.mironov.backendservice.repository;

import dev.mironov.backendservice.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
    Optional<UserModel> findUserByEmail(String email);
    Boolean existsByEmail(String email);
}

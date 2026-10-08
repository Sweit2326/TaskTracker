package dev.mironov.backendservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@NoArgsConstructor
public class UserModel {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Getter
    @Column(nullable = false, length = 255)
    private String password;

    public UserModel(String email, String pass) {
        this.email = email;
        this.password = pass;
    }
}

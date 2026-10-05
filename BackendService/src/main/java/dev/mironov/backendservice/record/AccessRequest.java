package dev.mironov.backendservice.record;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccessRequest(
        @NotEmpty(message = "Name cannot be empty!")
        @Pattern(regexp = "\\S+", message = "Spaces not allowed")
        @Size(max = 256, message = "Username is too long, maximum is 255 symbols")
        String username,
        @NotEmpty(message = "Password cannot be empty!")
        @Pattern(regexp = "\\S+", message = "Spaces not allowed")
        @Size(min = 4, max = 255, message = "Password must be between 4 and 255 symbols")
        String password
) {}


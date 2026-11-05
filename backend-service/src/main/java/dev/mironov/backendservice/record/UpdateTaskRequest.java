package dev.mironov.backendservice.record;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @Size(max = 255, message = "Title is too long, maximum is 255 symbols")
        @NotEmpty(message = "Title is empty, must be at least 1 symbol")
        String title,
        String content,
        @Pattern(regexp = "^$|PENDING|COMPLETED", message = "Status can be only PENDING or COMPLETED")
        String status
) {}

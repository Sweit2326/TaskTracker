package dev.mironov.backendservice.record;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponse(
        String title,
        String content,
        String status,
        Instant completed_at,
        UUID uuid
) {}

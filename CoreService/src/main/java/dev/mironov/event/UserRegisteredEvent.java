package dev.mironov.event;

import java.time.Instant;

public record UserRegisteredEvent(
        Integer eventId,
        String payload,
        Instant timestamp
) {}
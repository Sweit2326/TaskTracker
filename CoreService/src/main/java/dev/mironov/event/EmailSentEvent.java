package dev.mironov.event;

import java.time.Instant;

public record EmailSentEvent (
        Integer eventId,
        String email,
        String title,
        String content,
        Instant timestamp
) implements KafkaEvent {}
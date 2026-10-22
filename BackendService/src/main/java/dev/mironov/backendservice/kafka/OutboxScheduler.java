package dev.mironov.backendservice.kafka;

import dev.mironov.backendservice.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 10_000)
    public void run() {
        outboxService.processAll();
    }
}

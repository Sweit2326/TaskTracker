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
    public void processTasks() {
        outboxService.processAll();
        outboxService.processStuckProcessingTasks();
    }

    @Scheduled(fixedDelay = 60_000)
    public void checkLimitExceed() {
        outboxService.checkDeadLimitExceed(10);
        outboxService.checkRetryLimitExceed(15);
    }

    @Scheduled(fixedDelay = 600_000)
    public void purgeExpired() {
        outboxService.purgeExpiredTasks();
    }
}

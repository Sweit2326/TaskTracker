package dev.mironov.backendservice.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
        prefix = "app.features",
        name = "outbox-scheduler",
        havingValue = "true",
        matchIfMissing = true
)
public class SchedulerConfig {}
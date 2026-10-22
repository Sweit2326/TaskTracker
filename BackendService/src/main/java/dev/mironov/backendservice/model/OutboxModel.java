package dev.mironov.backendservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Outbox")
@NoArgsConstructor
public class OutboxModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @NotBlank
    private String payload;

    @Getter
    @NotBlank
    @Column(name = "event_type")
    private String eventType;

    @Getter
    @NotBlank
    private String source;

    public OutboxModel(String payload, String eventType, String source) {
        this.payload = payload;
        this.eventType = eventType;
        this.source = source;
    }
}

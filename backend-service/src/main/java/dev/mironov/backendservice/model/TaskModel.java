package dev.mironov.backendservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "Tasks")
@NoArgsConstructor
@Getter
public class TaskModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @NotBlank
    @Size(max = 255, message = "max limit for title is 255 characters")
    private String title;

    @Setter
    @Size(max = 255, message = "max limit for title is 255 characters")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserModel user;

    private Status status;
    private Instant completed_at;
    private UUID uuid;

    public TaskModel(String title, String content, UserModel userProxy) {
        this.title = title;
        this.content = content;
        this.user = userProxy;
        this.status = Status.PENDING;
        this.uuid = UUID.randomUUID();
    }

    public enum Status {
        COMPLETED,
        PENDING
    }

    public void setStatus(String state) {
        Status statusState = Status.valueOf(state);
        switch (statusState) {
            case COMPLETED -> {
                completed_at = Instant.now();
                status = Status.COMPLETED;
            }
            case PENDING -> {
                completed_at = null;
                status = Status.PENDING;
            }
        }
    }
}



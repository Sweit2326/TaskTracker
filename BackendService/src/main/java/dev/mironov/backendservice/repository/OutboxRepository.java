package dev.mironov.backendservice.repository;

import dev.mironov.backendservice.model.OutboxModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxModel, Integer> {
    List<OutboxModel> findTop10ByStatusOrStatus(OutboxModel.Status status, OutboxModel.Status status1);
    List<OutboxModel> findAllByProcessingUntilBefore(Instant time);
    Integer countAllByStatus(OutboxModel.Status status);

    @Modifying
    @Query(value = "DELETE FROM Outbox WHERE status = :status AND created_at < DATE_SUB(NOW(), INTERVAL :ttlSeconds SECOND) LIMIT :limit", nativeQuery = true)
    Integer deleteAllExpiredByStatus(
            @Param("status") OutboxModel.Status status,
            @Param("ttlSeconds") int ttlSeconds,
            @Param("limit") int limit
    );
}
package dev.mironov.backendservice.repository;

import dev.mironov.backendservice.model.OutboxModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxModel, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<OutboxModel> findTop10ByStatus(OutboxModel.Status status);
}
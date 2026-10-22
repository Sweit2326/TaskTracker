package dev.mironov.backendservice.repository;

import dev.mironov.backendservice.model.OutboxModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxModel, Integer> {
    List<OutboxModel> findAll();
}
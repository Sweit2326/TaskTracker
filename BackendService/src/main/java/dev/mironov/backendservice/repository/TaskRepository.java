package dev.mironov.backendservice.repository;

import dev.mironov.backendservice.model.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskModel, Integer> {
    List<TaskModel> getTaskModelsByUser_Id(Integer ownerId);
    Boolean existsByUuidAndUser_Id(UUID uuid, Integer ownerId);
    TaskModel getTaskModelByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);
}

package dev.mironov.backendservice.service;


import dev.mironov.backendservice.exception.TaskNotFoundException;
import dev.mironov.backendservice.model.TaskModel;
import dev.mironov.backendservice.model.UserModel;
import dev.mironov.backendservice.record.TaskRequest;
import dev.mironov.backendservice.record.TaskResponse;
import dev.mironov.backendservice.repository.TaskRepository;
import dev.mironov.backendservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<TaskResponse> getAll(int ownerId) {
        List<TaskModel> rawTasks = taskRepository.getTaskModelsByUser_Id(ownerId);
        List<TaskResponse> respTasks = new ArrayList<>();

        for (TaskModel task : rawTasks) {
            respTasks.add(new TaskResponse(
                    task.getTitle(),
                    task.getContent(),
                    task.getStatus().toString(),
                    task.getCompleted_at(),
                    task.getUuid()
            ));
        }
        return respTasks;
    }

    public TaskResponse create(int ownerId, TaskRequest req) {
        UserModel userProxy = userRepository.getReferenceById(ownerId);
        TaskModel task = new TaskModel(req.title(), req.content(), userProxy);

        taskRepository.save(task);
        return new TaskResponse(
                task.getTitle(),
                task.getContent(),
                task.getStatus().toString(),
                task.getCompleted_at(),
                task.getUuid());
    }

    public TaskResponse update(int ownerId, UUID uuid, TaskRequest req) {
        if (taskRepository.existsByUuidAndUser_Id(uuid, ownerId)) {
            TaskModel task = taskRepository.getTaskModelByUuid(uuid);
            task.setTitle(req.title());
            task.setContent(req.content());
            if (req.status() != null) task.setStatus(req.status());

            taskRepository.save(task);
            return new TaskResponse(
                    task.getTitle(),
                    task.getContent(),
                    task.getStatus().toString(),
                    task.getCompleted_at(),
                    task.getUuid());
        } else throw new TaskNotFoundException("Invalid task uuid");
    }

    public void delete(int ownerId, UUID uuid) {
        if (taskRepository.existsByUuidAndUser_Id(uuid, ownerId)) {
            taskRepository.deleteByUuid(uuid);
        } else throw new TaskNotFoundException("Invalid task uuid");
    }
}

package dev.mironov.backendservice.util;

import dev.mironov.backendservice.model.TaskModel;
import dev.mironov.backendservice.model.UserModel;
import dev.mironov.backendservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestTaskFactory {
    private final TaskRepository taskRepository;

    public TaskModel createTask(UserModel userProxy) {
        return taskRepository.save(new TaskModel("testTitle", "content", userProxy));
    }
}
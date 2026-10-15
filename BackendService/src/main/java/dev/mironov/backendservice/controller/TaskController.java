package dev.mironov.backendservice.controller;

import dev.mironov.backendservice.record.TaskRequest;
import dev.mironov.backendservice.record.TaskResponse;
import dev.mironov.backendservice.security.SecurityUtil;
import dev.mironov.backendservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks() {
        List<TaskResponse> resps = taskService.getAll(securityUtil.getUserId());
        return ResponseEntity.ok().body(resps);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest req) {
        TaskResponse resp = taskService.create(securityUtil.getUserId(), req);
        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable("uuid") UUID uuid, @Valid @RequestBody TaskRequest req) {
        TaskResponse resp = taskService.update(securityUtil.getUserId(), uuid, req);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTask(@PathVariable("uuid") UUID uuid) {
        taskService.delete(securityUtil.getUserId(), uuid);
        return ResponseEntity.noContent().build();
    }
}

package dev.mironov.backendservice;

import dev.mironov.backendservice.model.TaskModel;
import dev.mironov.backendservice.model.UserModel;
import dev.mironov.backendservice.record.CreateTaskRequest;
import dev.mironov.backendservice.record.UpdateTaskRequest;
import dev.mironov.backendservice.security.SecurityUtil;
import dev.mironov.backendservice.service.JwtService;
import dev.mironov.backendservice.util.TestTaskFactory;
import dev.mironov.backendservice.util.TestUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static dev.mironov.backendservice.util.TestUserFactory.generateTooLongRequest;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestContainerConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser
public class TaskIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TestUserFactory userFactory;
    @Autowired private TestTaskFactory taskFactory;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private SecurityUtil securityUtil;

    private UserModel userProxy;

    @BeforeEach
    void init() {
        userProxy = userFactory.createRandomUser();
        Mockito.when(securityUtil.getUserId()).thenReturn(userProxy.getId());
    }

    @Test
    void getTasks_returnsOk() throws Exception {
        TaskModel task = taskFactory.createTask(userProxy);
        mockMvc.perform(get("/api/tasks"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].title").value(task.getTitle()),
                        jsonPath("$[0].content").value(task.getContent()),
                        jsonPath("$[0].status").value(task.getStatus().toString()),
                        jsonPath("$[0].uuid").value(task.getUuid().toString())
                );
    }

    @Test
    void getTasks_withEmptyList_returnsOk() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(0))
                );
    }

    @Test
    void createTask_withMinimumData_returnsCreated() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("tasky", null);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.title").value(req.title()),
                        jsonPath("$.status").value("PENDING"),
                        jsonPath("$.uuid").isNotEmpty()
                );
    }

    @Test
    void createTask_withMaximumData_returnsCreated() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("tasky", "content");
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.title").value(req.title()),
                        jsonPath("$.content").value(req.content()),
                        jsonPath("$.status").value("PENDING"),
                        jsonPath("$.uuid").isNotEmpty()
                );
    }

    @Test
    void createTask_withEmptyTitle_returnsBadRequest() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest("", null);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_withTooLongTitle_returnsBadRequest() throws Exception {
        CreateTaskRequest req = new CreateTaskRequest(generateTooLongRequest(), null);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_setCompleted_returnsOk() throws Exception {
        TaskModel task = taskFactory.createTask(userProxy);
        UpdateTaskRequest req = new UpdateTaskRequest(task.getTitle(), task.getContent(), "COMPLETED");
        mockMvc.perform(patch("/api/tasks/"+task.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title").value(task.getTitle()),
                        jsonPath("$.content").value(task.getContent()),
                        jsonPath("$.status").value("COMPLETED"),
                        jsonPath("$.completed_at").isNotEmpty(),
                        jsonPath("$.uuid").value(task.getUuid().toString())
                );
    }

    @Test
    void updateTask_withMultipleFields_returnsOk() throws Exception {
        TaskModel task = taskFactory.createTask(userProxy);
        UpdateTaskRequest req = new UpdateTaskRequest("newTitle", "newContent", "COMPLETED");
        mockMvc.perform(patch("/api/tasks/"+task.getUuid())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.title").value(req.title()),
                        jsonPath("$.content").value(req.content()),
                        jsonPath("$.status").value("COMPLETED"),
                        jsonPath("$.completed_at").isNotEmpty(),
                        jsonPath("$.uuid").value(task.getUuid().toString())
                );
    }

    @Test
    void updateTask_withNonExistUUID_returnsNotFound() throws Exception {
        UpdateTaskRequest req = new UpdateTaskRequest("title", null, null);
        mockMvc.perform(patch("/api/tasks/"+"f47ac10b-58cc-4372-a567-0e02b2c3d479")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_withInvalidUUID_returnsBadRequest() throws Exception {
        UpdateTaskRequest req = new UpdateTaskRequest("title", null, null);
        mockMvc.perform(patch("/api/tasks/"+"invalid-test-uuid-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTask_returnsNoContent() throws Exception {
        TaskModel task = taskFactory.createTask(userProxy);
        mockMvc.perform(delete("/api/tasks/"+task.getUuid()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_twice_returnsNotFound() throws Exception {
        TaskModel task = taskFactory.createTask(userProxy);
        mockMvc.perform(delete("/api/tasks/"+task.getUuid()))
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(delete("/api/tasks/"+task.getUuid()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_whenNotOwner_returnsNotFound() throws Exception {
        TaskModel task = taskFactory.createTask(userFactory.createRandomUser());
        mockMvc.perform(delete("/api/tasks/"+task.getUuid()))
                .andExpect(status().isNotFound());
    }
}

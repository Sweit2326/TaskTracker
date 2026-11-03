package dev.mironov.backendservice;

import dev.mironov.backendservice.record.AccessRequest;
import dev.mironov.backendservice.service.RegisterService;
import dev.mironov.backendservice.util.TestDataFactory;
import dev.mironov.backendservice.util.TestJwtFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestContainerConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
public class AuthIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TestJwtFactory jwtFactory;
    @Autowired private TestDataFactory userFactory;
    @MockitoSpyBean private RegisterService registerService;

    private final AccessRequest testRequest = new AccessRequest("test@mail.com", "test");

    @Test
    void register_returnsOk() throws Exception {
        Mockito.doNothing().when(registerService).sendRegisterEmail(any());
        MvcResult result = mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andReturn();
        Mockito.verify(registerService, Mockito.times(1))
                .sendRegisterEmail(any());
        String token = result.getResponse().getHeader("Authorization");
        mockMvc.perform(get("/api/user")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void register_returnsConflict() throws Exception {
        userFactory.createUser(testRequest.email(), testRequest.password());
        Mockito.doNothing().when(registerService).sendRegisterEmail(any());
        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_returnsOk() throws Exception {
        userFactory.createUser(testRequest.email(), testRequest.password());
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andReturn();
        String token = result.getResponse().getHeader("Authorization");
        mockMvc.perform(get("/api/user")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void login_returnsUnauthorized() throws Exception {
        userFactory.createUser(testRequest.email(), testRequest.password());
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@mail.com\", \"password\": \"TEST\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_returnsEmail() throws Exception {
        userFactory.createUser(testRequest.email(), testRequest.password());
        String token = jwtFactory.generateValidToken(testRequest.email());
        mockMvc.perform(get("/api/user")
                        .header("Authorization", "Bearer " + token))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.email").value(testRequest.email())
                );
    }

    @Test
    void getUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_withExpiredToken_returnsUnauthorized() throws Exception {
        String token = jwtFactory.generateExpiredToken(testRequest.email());
        mockMvc.perform(get("/api/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_withChangedTokenPayload_returnsUnauthorized() throws Exception {
        String token = jwtFactory.generateTokenWithChangedPayload(testRequest.email());
        mockMvc.perform(get("/api/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_withInvalidTokenSign_returnsUnauthorized() throws Exception {
        String token = jwtFactory.generateTokenWithInvalidSign(testRequest.email());
        mockMvc.perform(get("/api/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_withTokenWithoutSubject_returnsUnauthorized() throws Exception {
        String token = jwtFactory.generateTokenWithoutSubject();
        mockMvc.perform(get("/api/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUser_withInvalidToken_returnsUnauthorized() throws Exception {
        String token = jwtFactory.generateInvalidToken();
        mockMvc.perform(get("/api/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}

package dev.mironov.backendservice;

import dev.mironov.backendservice.controller.AuthController;
import dev.mironov.backendservice.record.AccessRequest;
import dev.mironov.backendservice.service.JwtService;
import dev.mironov.backendservice.service.RegisterService;
import dev.mironov.backendservice.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static dev.mironov.backendservice.util.TestUserFactory.generateTooLongRequest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(AuthController.class)
public class AuthValidationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private RegisterService registerService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private AuthenticationManager authManager;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(registerService, jwtService, authManager, userDetailsService);
    }

    @Test
    void whenEmailIsIncorrect_returnsBadRequest() throws Exception {
        AccessRequest testRequest = new AccessRequest("email.com", "password");

        mockMvc.perform(post("/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenEmailIsTooLong_returnsBadRequest() throws Exception {
        AccessRequest testRequest = new AccessRequest(generateTooLongRequest(), "password");

        mockMvc.perform(post("/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenEmailIsEmpty_returnsBadRequest() throws Exception {
        AccessRequest testRequest = new AccessRequest("", "password");

        mockMvc.perform(post("/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPasswordIsIncorrect_returnsBadRequest() throws Exception {
        AccessRequest testRequest = new AccessRequest("email@x.com", "pa ss");

        mockMvc.perform(post("/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPasswordIsTooLong_returnsBadRequest() throws Exception {
        AccessRequest testRequest = new AccessRequest("email@x.com", generateTooLongRequest());

        mockMvc.perform(post("/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPasswordIsShort_returnsBadRequest() throws Exception {
        AccessRequest testRequest = new AccessRequest("email@x.com", "pas");

        mockMvc.perform(post("/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPasswordIsEmpty_returnsBadRequest() throws Exception {
        AccessRequest testRequest = new AccessRequest("email@x.com", "");

        mockMvc.perform(post("/api/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }
}

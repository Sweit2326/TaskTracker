package dev.mironov.backendservice;

import dev.mironov.backendservice.service.RegisterService;
import dev.mironov.backendservice.util.TestJwtFactory;
import dev.mironov.backendservice.util.TestUserFactory;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BackendTestConfiguration.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class AuthenticationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestJwtFactory jwtFactory;
    @Autowired
    private TestUserFactory userFactory;
    @MockitoSpyBean
    private RegisterService registerService;

    @Test
    void register_returnsOk() throws Exception {
        Mockito.doNothing().when(registerService).sendRegisterEmail(any());
        MvcResult result = mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@mail.com\", \"password\": \"test\"}"))
                .andExpect(status().isOk())
                .andReturn();
        Mockito.verify(registerService, Mockito.times(1))
                .sendRegisterEmail(any());
        String token = result.getResponse().getHeader("Authorization");
        mockMvc.perform(get("/api/user")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }
}

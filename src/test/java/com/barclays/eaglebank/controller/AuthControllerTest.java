package com.barclays.eaglebank.controller;

import com.barclays.eaglebank.auth.JwtUtil;
import com.barclays.eaglebank.exceptions.GlobalControllerAdvice;
import com.barclays.eaglebank.exceptions.NotFoundException;
import com.barclays.eaglebank.model.response.UserResponse;
import com.barclays.eaglebank.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(jwtUtil, userService))
                .setControllerAdvice(new GlobalControllerAdvice())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_ValidEmail_ReturnsTokenAndUserId() throws Exception {
        Map<String, String> requestBody = Map.of("email", "john.doe@example.com");

        UserResponse user = UserResponse.builder()
                .id("usr-123")
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(userService.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("usr-123")).thenReturn("jwt-token-123");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());
    }

    @Test
    void login_UserNotFound_ReturnsNotFound() throws Exception {
        Map<String, String> requestBody = Map.of("email", "nonexistent@example.com");

        when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_MissingEmail_ReturnsBadRequest() throws Exception {
        Map<String, String> requestBody = Map.of("otherField", "value");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }
}

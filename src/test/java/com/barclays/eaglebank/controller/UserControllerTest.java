package com.barclays.eaglebank.controller;

import com.barclays.eaglebank.exceptions.GlobalControllerAdvice;
import com.barclays.eaglebank.model.Address;
import com.barclays.eaglebank.model.requests.CreateUserRequest;
import com.barclays.eaglebank.model.requests.UpdateUserRequest;
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
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserService userService;

    MockMvc mockMvc;
    private final ObjectMapper json = new ObjectMapper();

    @BeforeEach
    void setup() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .setControllerAdvice(new GlobalControllerAdvice())
                .setValidator(validator)
                .build();
    }

    @Test
    void createUser_created() throws Exception {
        var req = CreateUserRequest.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .phoneNumber("+441234567890")
                .address(Address.builder()
                        .line1("1 High St").town("London").county("Greater London").postcode("SW1A 1AA")
                        .build())
                .build();

        var resp = UserResponse.builder().id("usr-abc123").name("Jane Doe").email("jane@example.com").build();
        when(userService.create(any(CreateUserRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_badJson_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Jane\", "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fetchUser_ok_whenOwner() throws Exception {
        var resp = UserResponse.builder()
                .id("usr-me")
                .name("Me")
                .email("me@example.com")
                .build();
        when(userService.findById("usr-me")).thenReturn(Optional.of(resp));

        mockMvc.perform(get("/v1/users/usr-me").requestAttr("authUserId", "usr-me"))
                .andExpect(status().isOk());
    }

    @Test
    void fetchUser_notFound() throws Exception {
        when(userService.findById("usr-missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/users/usr-missing").requestAttr("authUserId", "usr-missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void fetchUser_forbidden_whenDifferentUser() throws Exception {
        var resp = UserResponse.builder().id("usr-a").name("A").build();
        when(userService.findById("usr-a")).thenReturn(Optional.of(resp));

        mockMvc.perform(get("/v1/users/usr-a").requestAttr("authUserId", "usr-b"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_ok_whenOwner() throws Exception {
        var existing = UserResponse.builder().id("usr-me").name("Old").email("old@example.com").build();
        var updated = UserResponse.builder().id("usr-me").name("New").build();

        when(userService.findById("usr-me")).thenReturn(Optional.of(existing));
        when(userService.update(eq("usr-me"), any(UpdateUserRequest.class))).thenReturn(updated);

        var req = UpdateUserRequest.builder().name("New").build();

        mockMvc.perform(patch("/v1/users/usr-me")
                        .requestAttr("authUserId", "usr-me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_notFound() throws Exception {
        var req = UpdateUserRequest.builder().name("X").build();
        when(userService.findById("usr-missing")).thenReturn(Optional.empty());

        mockMvc.perform(patch("/v1/users/usr-missing")
                        .requestAttr("authUserId", "usr-missing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_forbidden_whenDifferentUser() throws Exception {
        var existing = UserResponse.builder().id("usr-a").name("A").build();
        when(userService.findById("usr-a")).thenReturn(Optional.of(existing));

        var req = UpdateUserRequest.builder().name("Nope").build();

        mockMvc.perform(patch("/v1/users/usr-a")
                        .requestAttr("authUserId", "usr-b")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUser_noContent_whenOwner() throws Exception {
        var existing = UserResponse.builder().id("usr-me").name("Me").build();
        when(userService.findById("usr-me")).thenReturn(Optional.of(existing));
        doNothing().when(userService).delete("usr-me");

        mockMvc.perform(delete("/v1/users/usr-me").requestAttr("authUserId", "usr-me"))
                .andExpect(status().isNoContent());

    }

    @Test
    void deleteUser_notFound() throws Exception {
        when(userService.findById("usr-missing")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/v1/users/usr-missing").requestAttr("authUserId", "usr-missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_forbidden_whenDifferentUser() throws Exception {
        var existing = UserResponse.builder().id("usr-a").build();
        when(userService.findById("usr-a")).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/v1/users/usr-a").requestAttr("authUserId", "usr-b"))
                .andExpect(status().isForbidden());
    }
}
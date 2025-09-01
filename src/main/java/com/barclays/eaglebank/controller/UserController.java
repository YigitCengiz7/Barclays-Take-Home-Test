package com.barclays.eaglebank.controller;

import com.barclays.eaglebank.model.requests.CreateUserRequest;
import com.barclays.eaglebank.model.requests.UpdateUserRequest;
import com.barclays.eaglebank.model.response.UserResponse;
import com.barclays.eaglebank.exceptions.ForbiddenException;
import com.barclays.eaglebank.exceptions.NotFoundException;
import jakarta.validation.constraints.Pattern;
import com.barclays.eaglebank.service.user.UserService;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;


@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest body) {
        var user = userService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> fetchUser(
            @RequestAttribute("authUserId") String authUserId,
            @PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId) {

        var user = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException("User was not found"));

        if (!authUserId.equals(userId)) {
            throw new ForbiddenException("Forbidden");
        }
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @RequestAttribute("authUserId") String authUserId,
            @PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId,
            @Valid @RequestBody UpdateUserRequest body) {

        userService.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));

        if (!authUserId.equals(userId)) {
            throw new ForbiddenException("Forbidden");
        }

        var updated = userService.update(userId, body);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @RequestAttribute("authUserId") String authUserId,
            @PathVariable @Pattern(regexp = "^usr-[A-Za-z0-9]+$") String userId) {

        userService.findById(userId).orElseThrow(() -> new NotFoundException("User was not found"));

        if (!authUserId.equals(userId)) {
            throw new ForbiddenException("Forbidden");
        }

        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
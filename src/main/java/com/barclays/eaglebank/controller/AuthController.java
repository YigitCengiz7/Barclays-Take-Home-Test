package com.barclays.eaglebank.controller;

import com.barclays.eaglebank.auth.JwtUtil;
import com.barclays.eaglebank.exceptions.NotFoundException;
import com.barclays.eaglebank.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody Map<String, String> body) {
        String email = Optional.ofNullable(body.get("email"))
                .map(String::trim)
                .map(String::toLowerCase)
                .orElseThrow(() -> new IllegalArgumentException("Email is required"));

        var user = userService.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(Map.of("token", token, "userId", user.getId()));
    }
}

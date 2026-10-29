package com.trafficiq.controller;

import com.trafficiq.dto.ApiResponse;
import com.trafficiq.dto.LoginRequest;
import com.trafficiq.dto.LoginResponse;
import com.trafficiq.entity.User;
import com.trafficiq.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful.", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        return authService.getCurrentAuthenticatedUser()
                .map(u -> ResponseEntity.ok(ApiResponse.ok("Authenticated user retrieved.", Map.<String, Object>of(
                        "userId", u.getUserId(),
                        "name", u.getName(),
                        "email", u.getEmail(),
                        "role", u.getRole().name()
                ))))
                .orElse(ResponseEntity.status(401).body(ApiResponse.error("Unauthorized.")));
    }
}

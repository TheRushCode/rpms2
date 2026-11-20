package com.kiranacademy.rpms.controller;

import com.kiranacademy.rpms.dto.auth.LoginRequest;
import com.kiranacademy.rpms.dto.auth.LoginResponse;
import com.kiranacademy.rpms.dto.auth.RegisterStaffRequest;
import com.kiranacademy.rpms.dto.common.ApiResponse;
import com.kiranacademy.rpms.entity.User;
import com.kiranacademy.rpms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUserProfile() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("username", user.getUsername());
        map.put("fullName", user.getFullName());
        map.put("email", user.getEmail());
        map.put("role", user.getRole().getRoleCode());
        map.put("status", user.getStatus());
        map.put("lastLoginAt", user.getLastLoginAt());
        return ResponseEntity.ok(ApiResponse.ok(map));
    }

    @PostMapping("/register-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> registerStaff(@Valid @RequestBody RegisterStaffRequest request) {
        User created = authService.registerStaff(request, getCurrentUser());
        return ResponseEntity.ok(ApiResponse.ok("Staff member registered successfully", created));
    }
}

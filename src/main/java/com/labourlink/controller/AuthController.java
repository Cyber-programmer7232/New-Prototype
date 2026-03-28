package com.labourlink.controller;

import com.labourlink.dto.AuthResponse;
import com.labourlink.dto.LoginRequest;
import com.labourlink.dto.RegisterRequest;
import com.labourlink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // JWT is stateless; logout is done client-side by removing the token
        return ResponseEntity.ok("Logged out successfully");
    }
}

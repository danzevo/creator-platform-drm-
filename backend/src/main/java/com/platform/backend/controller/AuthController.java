package com.platform.backend.controller;

import com.platform.backend.dto.AuthDtos.*;
import com.platform.backend.entity.CreatorProfile;
import com.platform.backend.entity.User;
import com.platform.backend.repository.CreatorProfileRepository;
import com.platform.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CreatorProfileRepository profileRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        CreatorProfile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        return ResponseEntity.ok(Map.of("id", user.getId(),
                                        "email", user.getEmail(),
                                        "role", user.getRole().name(),
                                        "username", profile != null ? profile.getUsername() : "",
                                        "displayName", profile != null ? profile.getDisplayName() : ""
                                ));
    }
}

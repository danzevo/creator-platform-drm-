package com.platform.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class AuthDtos {
    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class RegisterRequest {
        @Email @NotBlank
        private String email;

        @NotBlank @Size(min = 6)
        private String password;

        @NotBlank @Size(min = 3, max = 30)
        private String username;

        @NotBlank
        private String displayName;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        @Email @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class AuthResponse {
        private String token;
        private String role;
        private String email;
        private String username;
        private String displayName;
    }
}
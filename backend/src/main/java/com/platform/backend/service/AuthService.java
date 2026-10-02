package com.platform.backend.service;

import com.platform.backend.dto.AuthDtos.AuthResponse;
import com.platform.backend.dto.AuthDtos.LoginRequest;
import com.platform.backend.dto.AuthDtos.RegisterRequest;
import com.platform.backend.entity.CreatorProfile;
import com.platform.backend.entity.Role;
import com.platform.backend.entity.User;
import com.platform.backend.repository.CreatorProfileRepository;
import com.platform.backend.repository.UserRepository;
import com.platform.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final CreatorProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (profileRepository.existsByUsername(req.getUsername().toLowerCase())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = userRepository.save(User.builder()
                                        .email(req.getEmail())
                                        .password(passwordEncoder.encode(req.getPassword()))
                                        .role(Role.CREATOR)
                                        .build()
                                        );
        
        CreatorProfile profile = profileRepository.save(CreatorProfile.builder()
                                                                    .user(user)
                                                                    .username(req.getUsername().toLowerCase())
                                                                    .displayName(req.getDisplayName())
                                                                    .bio("Welcome to my digital storefront!")
                                                                    .themeConfigJson("{\"bg\":\"#0f172a\",\"primary\":\"#38bdf8\",\"font\":\"Inter\"}")
                                                                    .build()
                                                        );
        
        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder().token(token)
                                    .role(user.getRole().name())
                                    .email(user.getEmail())
                                    .username(profile.getUsername())
                                    .displayName(profile.getDisplayName())
                                    .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        CreatorProfile profile = profileRepository.findByUserId(user.getId()).orElse(null);
        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                            .token(token)
                            .role(user.getRole().name())
                            .email(user.getEmail())
                            .username(profile != null ? profile.getUsername() : "")
                            .displayName(profile != null ? profile.getDisplayName() : "")
                            .build();
    }
}
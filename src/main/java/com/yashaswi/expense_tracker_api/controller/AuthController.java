package com.yashaswi.expense_tracker_api.controller;

import com.yashaswi.expense_tracker_api.dto.auth.AuthResponse;
import com.yashaswi.expense_tracker_api.dto.auth.LoginRequest;
import com.yashaswi.expense_tracker_api.dto.auth.SignupRequest;
import com.yashaswi.expense_tracker_api.dto.refreshtoken.RefreshTokenRequest;
import com.yashaswi.expense_tracker_api.dto.refreshtoken.RefreshTokenResponse;
import com.yashaswi.expense_tracker_api.entity.RefreshToken;
import com.yashaswi.expense_tracker_api.entity.User;
import com.yashaswi.expense_tracker_api.enums.Role;
import com.yashaswi.expense_tracker_api.repository.RefreshTokenRepository;
import com.yashaswi.expense_tracker_api.repository.UserRepository;
import com.yashaswi.expense_tracker_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, "User name is already taken"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .status((HttpStatus.BAD_REQUEST))
                    .body(new AuthResponse(null, "Email is already registered"));
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(Role.USER).build();

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(null, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<RefreshTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Get User entity for token pair
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // ✅ NEW: Token PAIR!
            RefreshTokenResponse tokens = jwtService.generateTokenPair(user);
            return ResponseEntity.ok(tokens);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RefreshTokenResponse.builder()
                            .accessToken(null)
                            .refreshToken(null)
                            .build());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken oldRefresh = jwtService.validateRefreshToken(refreshTokenRequest.getRefreshToken());

        oldRefresh.setRevoked(true);
        refreshTokenRepository.save(oldRefresh);

        RefreshTokenResponse newTokens = jwtService.generateTokenPair(oldRefresh.getUser());

        return ResponseEntity.ok(Map.of(
                "accessToken", newTokens.getAccessToken(),
                "refreshToken", newTokens.getRefreshToken()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = jwtService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return ResponseEntity.ok("Logged out");
    }


}

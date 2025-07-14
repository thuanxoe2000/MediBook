package com.example.MediBook.controller;

import com.example.MediBook.configuration.CustomUserDetails;
import com.example.MediBook.dto.request.LoginRequest;
import com.example.MediBook.dto.request.UpdateRequest;
import com.example.MediBook.entity.BlacklistedToken;
import com.example.MediBook.entity.User;
import com.example.MediBook.enums.TokenType;
import com.example.MediBook.repository.BlacklistedTokenRepository;
import com.example.MediBook.security.JwtService;
import com.example.MediBook.service.RefreshTokenService;
import com.example.MediBook.service.TokenService;
import com.example.MediBook.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthenticationManager authenticationManager;
    JwtService jwtService;
    BlacklistedTokenRepository blacklist;
    UserService userService;
    TokenService tokenService;
    RefreshTokenService refreshTokenService;

    @GetMapping("/verify-email")
    public void verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {
        boolean ok = userService.verifyToken(token);
        if (ok) {
            response.sendRedirect("/MediBook/login.html?verified=true");
        } else {
            response.sendRedirect("sdfgxhcvmvg");
        }
    }

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            if (userDetails.getUser() == null) {
                throw new RuntimeException("userDetails.getUser() が null を返しました");
            }

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = refreshTokenService.createOrUpdateRefreshToken(userDetails.getUser());

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            ));

        } catch (DisabledException e) {
            return ResponseEntity.status(403).body(Map.of("error", "アカウントがロックされています。管理者に連絡してください。"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "メールアドレスまたはパスワードが正しくありません"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "システムエラー: " + e.getMessage()));
        }
    }

    @GetMapping("/login")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        var tokenEntity = refreshTokenService.verifyExpiration(refreshToken);
        String newAccessToken = jwtService.generateToken(new CustomUserDetails(tokenEntity.getUser()));

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @GetMapping("/check-token")
    public ResponseEntity<?> checkToken(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of("message", "アクセストークンは有効です"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.isTokenValid(token)) {
                LocalDateTime exp = jwtService.extractExpiration(token);
                blacklist.save(new BlacklistedToken(null, token, exp));

                String email = jwtService.extractEmail(token);
                User user = userService.findByEmail(email).orElseThrow();
                refreshTokenService.deleteByUser(user);
            }
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendResetLink(@RequestParam String email) {
        userService.sendResetPasswordToken(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody UpdateRequest request) {
        Optional<User> userOpt = tokenService.validateToken(request.getToken(), TokenType.RESET_PASSWORD);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "無効なリクエストです"));
        }

        User user = userOpt.get();
        userService.resetPassword(request.getPassword(), user.getEmail());

        tokenService.invalidate(request.getToken());

        return ResponseEntity.ok().build();
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void cleanExpiredTokens() {
        blacklist.deleteAllByExpBefore(LocalDateTime.now());
    }
}

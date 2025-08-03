package com.example.MediBook.service;

import com.example.MediBook.entity.RefreshToken;
import com.example.MediBook.entity.User;
import com.example.MediBook.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenService {
    RefreshTokenRepository refreshTokenRepository;

    @NonFinal
    @Value("${jwt.refresh-expiration}")
    long refreshExpirationMs;

    @Transactional
    public String createOrUpdateRefreshToken(User user) {
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserId(user.getId());

        String newToken = UUID.randomUUID().toString();
        LocalDateTime exp = LocalDateTime.now().plusSeconds(refreshExpirationMs);

        RefreshToken token = existingTokenOpt.map(existing -> {
            existing.setToken(newToken);
            existing.setExp(exp);
            return existing;
        }).orElse(RefreshToken.builder()
                .token(newToken)
                .exp(exp)
                .user(user)
                .build());

        refreshTokenRepository.save(token);
        return newToken;
    }

    public RefreshToken verifyExpiration(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(t -> t.getExp().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new RuntimeException("リフレッシュトークンが無効か、期限切れです"));
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
    }

}

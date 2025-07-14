package com.example.MediBook.repository;

import com.example.MediBook.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken,Long> {
    boolean existsByToken(String token);
    void deleteAllByExpBefore(LocalDateTime time);
}

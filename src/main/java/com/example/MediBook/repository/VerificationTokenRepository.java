package com.example.MediBook.repository;

import com.example.MediBook.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByToken(String token);
    void deleteByUser_Id(Long userId);
}

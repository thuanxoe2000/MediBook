package com.example.MediBook.service;

import com.example.MediBook.entity.User;
import com.example.MediBook.entity.VerificationToken;
import com.example.MediBook.enums.TokenType;
import com.example.MediBook.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TokenService {
    VerificationTokenRepository verificationTokenRepository;

    public String createToken(User user, TokenType type){
        String token= UUID.randomUUID().toString();
        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setType(type);
        verificationToken.setExp(LocalDateTime.now().plusMinutes(15));

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public Optional<User> validateToken(String token,TokenType type){
        return verificationTokenRepository.findByToken(token)
                .filter(t->t.getType()==type)
                .filter(t->t.getExp().isAfter(LocalDateTime.now()))
                .map(VerificationToken::getUser);
    }

    public void deleteVerificationTokenByUser(Long userId){
        verificationTokenRepository.deleteByUser_Id(userId);
    }

    @Transactional
    public void invalidate(String token){
        verificationTokenRepository.deleteByToken(token);
    }
}

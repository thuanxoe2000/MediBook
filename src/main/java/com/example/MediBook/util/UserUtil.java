package com.example.MediBook.util;

import com.example.MediBook.configuration.CustomUserDetails;
import com.example.MediBook.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUtil {

    public CustomUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("⚠️ 未ログイン、または認証情報が null です");
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new RuntimeException("⚠️ principal は CustomUserDetails 型ではありません");
        }

        return userDetails;
    }

    public boolean hasRole(String role){
        return getCurrentUser().getRoles().stream()
                .anyMatch(r->r.getName().equalsIgnoreCase(role));
    }

    public User getCurrentUser() {
        return getCurrentUserDetails().getUser();
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}

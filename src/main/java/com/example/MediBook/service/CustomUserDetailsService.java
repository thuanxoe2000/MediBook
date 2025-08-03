package com.example.MediBook.service;

import com.example.MediBook.configuration.CustomUserDetails;
import com.example.MediBook.entity.User;
import com.example.MediBook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("メールアドレスが存在しません"));

        if (!user.isVerified()) {
            throw new BadCredentialsException("アカウントが確認されていません");
        }

        return new CustomUserDetails(user);
    }
}

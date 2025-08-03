package com.example.MediBook.configuration;

import com.example.MediBook.entity.User;
import com.example.MediBook.repository.RoleRepository;
import com.example.MediBook.repository.UserRepository;
import com.example.MediBook.security.JwtService;
import com.example.MediBook.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    JwtService jwtService;
    UserRepository userRepository;
    RefreshTokenService refreshTokenService;
    RoleRepository roleRepository;

    static final String FRONTEND_HOME_URL = "http://localhost:5500/MediBook/home.html";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            var role=roleRepository.findById("USER").orElseThrow(()->new RuntimeException("Role「USER」はデータベースに存在していません"));
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRoles(Set.of(role));
            newUser.setEnabled(true);
            return userRepository.save(newUser);
        });

        var userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = refreshTokenService.createOrUpdateRefreshToken(user);

        String redirectUrl = UriComponentsBuilder.fromUriString(FRONTEND_HOME_URL)
                .queryParam("token", accessToken)
                .queryParam("refresh", refreshToken)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}

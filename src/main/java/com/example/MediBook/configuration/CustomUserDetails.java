package com.example.MediBook.configuration;

import com.example.MediBook.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final User user;

    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = user.getRoles().stream()
                .flatMap(role -> {
                    Stream<GrantedAuthority> roleAuth =
                            Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                    Stream<GrantedAuthority> permAuth = role.getPermissions().stream()
                            .map(perm -> new SimpleGrantedAuthority(perm.getName()));
                    return Stream.concat(roleAuth, permAuth);
                })
                .distinct()
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getEmail(); }

    @Override
    public boolean isEnabled() { return user.isEnabled(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }
}

package com.catchsolmind.cheongyeonbe.global.security.jwt;

import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record JwtUserDetails(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // 지금은 권한 없음
    }

    @Override
    public String getPassword() {
        return null; // OAuth라 패스워드 없음
    }

    @Override
    public String getUsername() {
        return user.getUserId().toString();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

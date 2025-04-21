package com.example.demo.config.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String email;

    // 생성자
    public JwtAuthentication(String email, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.email = email;
        setAuthenticated(true);  // 인증된 것으로 설정
    }

    @Override
    public Object getCredentials() {
        return null;  // JWT 인증이므로 자격증명은 없고, null을 반환
    }

    @Override
    public Object getPrincipal() {
        return this.email;  // 이메일을 principal로 반환
    }

    // 이메일 반환
    public String getEmail() {
        return this.email;
    }
}
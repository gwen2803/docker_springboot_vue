package com.example.demo.config.jwt;

import com.example.demo.config.jwt.JwtAuthentication;
import com.example.demo.user.entity.Token;
import com.example.demo.user.repository.TokenRepository;

// import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, TokenRepository tokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 JWT 토큰을 가져오기
        String token = getJwtFromRequest(request);

        // // JWT 토큰이 존재하고 유효한지 확인
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // JWT 토큰에서 사용자 정보를 가져와서 인증 처리
            String email = jwtTokenProvider.getUserEmail(token);

            Token tokenEntity = tokenRepository.findByEmail(email).orElse(null);

            if (tokenEntity != null && tokenEntity.getAccessToken().equals(token)) {
                // 권한 목록 생성 (여기선 기본 "ROLE_USER"만 추가)
                Collection<GrantedAuthority> authorities = Collections
                        .singletonList(new SimpleGrantedAuthority("ROLE_USER"));

                // 인증 객체 생성
                JwtAuthentication authentication = new JwtAuthentication(email, authorities);

                // SecurityContext에 인증 정보 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 인증을 SecurityContext에 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 필터 체인에서 다음 필터로 전달
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 JWT 토큰을 추출하는 메서드
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 반환
        }
        return null;
    }
}
package com.example.demo.user.service;

import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.common.dto.ApiResponse;
import com.example.demo.user.dto.UpdateNicknameRequest;
import com.example.demo.user.dto.UserRegisterRequest;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 사용자 회원가입 처리
     */
    public User register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(); // 커스텀 예외 추천
        }

        User user = User.builder()
            .email(request.getEmail())
            .nickname(request.getNickname())
            .password(passwordEncoder.encode(request.getPassword()))
            .oauthUser(false)
            .build();

        return userRepository.save(user);
    }

    /**
     * 닉네임 업데이트
     */
    public void updateNickname(String accessToken, UpdateNicknameRequest request) {
        User user = findUserByToken(accessToken);
        user.setNickname(request.getNickname());
        userRepository.save(user);
    }

    /**
     * 회원 탈퇴 처리 (soft delete)
     */
    public void deleteUser(String accessToken) {
        User user = findUserByToken(accessToken);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * JWT 토큰에서 이메일 추출 후 사용자 조회
     */
    private User findUserByToken(String accessToken) {
        String email = jwtUtil.getEmailFromToken(accessToken);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}

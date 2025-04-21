package com.example.demo.auth.service;

import com.example.demo.auth.dto.*;
import com.example.demo.user.entity.User;
import com.example.demo.user.entity.Token;
import com.example.demo.user.entity.PasswordResetToken;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.repository.TokenRepository;
import com.example.demo.user.repository.PasswordResetTokenRepository;
import com.example.demo.user.service.EmailService;
import com.example.demo.config.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /** 로그인 */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        Token token = Token.builder()
                .email(user.getEmail())
                .accessToken(jwtTokenProvider.generateAccessToken(user.getEmail()))
                .refreshToken(jwtTokenProvider.generateRefreshToken(user.getEmail()))
                .build();

        tokenRepository.save(token);

        return LoginResponse.builder()
                .user(user)
                .token(token)
                .build();
    }

    /** 회원가입 */
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.findByEmailAndDeletedAtIsNull(request.getEmail()).isPresent()) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        Token token = Token.builder()
                .email(user.getEmail())
                .accessToken(jwtTokenProvider.generateAccessToken(user.getEmail()))
                .refreshToken(jwtTokenProvider.generateRefreshToken(user.getEmail()))
                .build();

        tokenRepository.save(token);

        return SignupResponse.builder()
                .user(user)
                .token(token)
                .build();
    }

    /** 로그아웃 */
    public void logout(String email) {
        Token token = tokenRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 토큰입니다."));
        tokenRepository.delete(token);
    }

    /** 비밀번호 재설정 요청 */
    public void sendPasswordResetEmail(SendPasswordResetEmailRequest request) {
        String email = request.getEmail();

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new RuntimeException("해당 이메일의 사용자가 존재하지 않습니다."));

        passwordResetTokenRepository.deleteByUser(user); // 기존 토큰 제거

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    /** 비밀번호 재설정 */
    public void resetPassword(ResetPasswordRequest request) {
        String accessToken = request.getAccessToken();
        String newPassword = request.getNewPassword();

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(accessToken)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    /** 비밀번호 변경 */
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /** 닉네임 변경 */
    public void changeNickname(String accessToken, String newNickname) {
        User user = getUserFromToken(accessToken);
        user.setNickname(newNickname);
        userRepository.save(user);
    }

    /** 사용자 정보 조회 */
    public User getUserInfo(String accessToken) {
        String email = jwtTokenProvider.getUserEmail(accessToken);
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    /** AccessToken 으로 사용자 조회 */
    private User getUserFromToken(String accessToken) {
        String email = jwtTokenProvider.getUserEmail(accessToken);
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    /** AccessToken 유효성 검사 */
    public boolean validateToken(String accessToken) {
        return jwtTokenProvider.validateToken(accessToken);
    }

    /** 토큰 갱신 */
    public Token refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            tokenRepository.deleteByRefreshToken(refreshToken);
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String email = jwtTokenProvider.getUserEmail(refreshToken);
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 토큰입니다."));

        tokenRepository.delete(token);

        Token newToken = Token.builder()
                .email(user.getEmail())
                .accessToken(jwtTokenProvider.generateAccessToken(user.getEmail()))
                .refreshToken(refreshToken)
                .build();

        tokenRepository.save(newToken);

        return newToken;
    }
}

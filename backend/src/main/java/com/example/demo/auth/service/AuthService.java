package com.example.demo.auth.service;

import com.example.demo.auth.dto.*;
import com.example.demo.common.service.ResponseService;
import com.example.demo.user.entity.User;
import com.example.demo.user.entity.PasswordResetToken;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.repository.PasswordResetTokenRepository;
import com.example.demo.user.service.EmailService;
import com.example.demo.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ResponseService responseService;

    /** 로그인 */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return LoginResponse.builder()
                .status(200)
                .message("로그인 성공")
                .json(LoginResponse.TokenData.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }

    /** 회원가입 */
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return SignupResponse.builder()
                    .status(400)
                    .message("이미 사용 중인 이메일입니다.")
                    .json(null)
                    .build();
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .oauthUser(false)
                .build();

        userRepository.save(user);

        return SignupResponse.builder()
                .status(200)
                .message("회원가입 성공")
                .json(null)
                .build();
    }

    /** 로그아웃 */
    public void logout(String accessToken) {
        String email = jwtUtil.getEmailFromToken(accessToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        user.setRefreshToken(null);
        userRepository.save(user);
    }

    /** 비밀번호 재설정 요청 */
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
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
    public void resetPassword(String accessToken, String newPassword) {
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
    public void changePassword(String accessToken, ChangePasswordRequest request) {
        User user = getUserFromToken(accessToken);

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
        String email = jwtUtil.getEmailFromToken(accessToken);
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    /** AccessToken 으로 사용자 조회 */
    private User getUserFromToken(String accessToken) {
        String email = jwtUtil.getEmailFromToken(accessToken);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}

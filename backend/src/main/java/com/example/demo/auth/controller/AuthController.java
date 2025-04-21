package com.example.demo.auth.controller;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.service.AuthService;
import com.example.demo.common.dto.ApiResponse;
import com.example.demo.config.jwt.JwtAuthentication;
import com.example.demo.user.entity.Token;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = authService.login(request);
            return ResponseEntity.ok(new ApiResponse<LoginResponse>(HttpStatus.OK.value(), "로그인 성공", loginResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<LoginResponse>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "로그인 실패: " + e.getMessage(), null));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody SignupRequest request) {
        try {
            SignupResponse signupResponse = authService.signup(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "회원가입 성공", signupResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "회원가입 실패: " + e.getMessage(),
                            null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal JwtAuthentication authentication) {
        try {
            authService.logout(authentication.getEmail());
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "로그아웃 성공", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "로그아웃 실패: " + e.getMessage(),
                            null));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody SendPasswordResetEmailRequest request) {
        try {
            authService.sendPasswordResetEmail(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "비밀번호 재설정 이메일을 보냈습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "비밀번호 재설정 이메일 전송 실패: " + e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "비밀번호가 성공적으로 변경되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 변경 실패: " + e.getMessage(),
                            null));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal JwtAuthentication authentication,
            @RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(authentication.getEmail(), request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "비밀번호가 성공적으로 변경되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 변경 실패: " + e.getMessage(),
                            null));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Token>> refreshToken(@RequestHeader("Authorization") String bearerToken) {
        if (!bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "잘못된 Authorization 헤더입니다.", null));
        }

        String refreshToken = bearerToken.replace("Bearer ", "");

        try {
            Token token = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "토큰이 성공적으로 갱신되었습니다.", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "토큰 갱신 실패: " + e.getMessage(),
                            null));
        }
    }

}
 
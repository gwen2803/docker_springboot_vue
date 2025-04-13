package com.example.demo.auth.controller;

import com.example.demo.auth.dto.*;
import com.example.demo.auth.service.AuthService;
import com.example.demo.common.dto.ApiResponse;
import com.example.demo.common.service.ResponseService;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;
    private final ResponseService responseService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(responseService.success("로그인 성공", authService.login(request)));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(responseService.success("회원가입 성공", authService.signup(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("Authorization") String bearerToken) {
        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            return ResponseEntity.badRequest().body(responseService.fail(400, "잘못된 Authorization 헤더입니다."));
        }

        try {
            String accessToken = bearerToken.substring(BEARER_PREFIX.length());
            authService.logout(accessToken);
            return ResponseEntity.ok(responseService.success("로그아웃 성공", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.fail(500, "로그아웃 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok(responseService.success("비밀번호 재설정 이메일을 보냈습니다.", null));
    }

    @PostMapping("/request-reset-password")
    public ResponseEntity<ApiResponse<?>> requestPasswordReset(@RequestBody Map<String, String> body) {
        authService.requestPasswordReset(body.get("email"));
        return ResponseEntity.ok(responseService.success("비밀번호 재설정 이메일을 보냈습니다.", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody Map<String, String> body) {
        authService.resetPassword(body.get("token"), body.get("newPassword"));
        return ResponseEntity.ok(responseService.success("비밀번호가 성공적으로 변경되었습니다.", null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @RequestHeader("Authorization") String authorization,
            @RequestBody ChangePasswordRequest request) {
        String accessToken = authorization.replace(BEARER_PREFIX, "");
        authService.changePassword(accessToken, request);
        return ResponseEntity.ok(responseService.success("비밀번호가 성공적으로 변경되었습니다.", null));
    }

    @PostMapping("/change-nickname")
    public ResponseEntity<ApiResponse<?>> changeNickname(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> body) {
        String accessToken = authorization.replace(BEARER_PREFIX, "");
        authService.changeNickname(accessToken, body.get("newNickname"));
        return ResponseEntity.ok(responseService.success("닉네임이 성공적으로 변경되었습니다.", null));
    }

    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<?>> getUserInfo(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.replace(BEARER_PREFIX, "");
        User user = authService.getUserInfo(accessToken);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", user.getEmail());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(responseService.success("회원 정보 조회 성공", userInfo));
    }

    @PostMapping("/oauth2/login")
    public ResponseEntity<ApiResponse<?>> oauthLogin(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(responseService.fail(400, "가입되지 않은 사용자입니다."));
        }

        User user = optionalUser.get();

        if (!user.isOauthUser()) {
            return ResponseEntity.badRequest().body(responseService.fail(400, "일반 로그인 사용자는 OAuth2 로그인을 사용할 수 없습니다."));
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Map<String, String> tokens = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

        return ResponseEntity.ok(responseService.success("OAuth2 로그인 성공", tokens));
    }

    @PostMapping("/oauth2/register")
    public ResponseEntity<ApiResponse<?>> oauthRegister(@RequestBody SignupRequest request) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            return ResponseEntity.badRequest().body(responseService.fail(400, "이미 가입된 이메일입니다."));
        }

        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password("") // OAuth2는 별도 비밀번호 없음
                .oauthUser(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(responseService.success("OAuth2 회원가입 성공", null));
    }
}

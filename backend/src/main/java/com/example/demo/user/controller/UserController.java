package com.example.demo.user.controller;

import com.example.demo.common.dto.ApiResponse;
import com.example.demo.user.dto.UserRegisterRequest;
import com.example.demo.user.dto.UpdateNicknameRequest;
import com.example.demo.user.entity.User;
import com.example.demo.user.service.UserService;
import com.example.demo.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ResponseService responseService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody UserRegisterRequest request) {
        try {
            User user = userService.register(request);

            Map<String, Object> json = Map.of("userId", user.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(responseService.success("회원가입 성공", json));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(responseService.fail(HttpStatus.CONFLICT.value(), e.getMessage()));
        }
    }

    @PutMapping("/nickname")
    public ResponseEntity<ApiResponse<?>> updateNickname(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody UpdateNicknameRequest request) {
        try {
            String accessToken = extractTokenOrThrow(authorization);
            userService.updateNickname(accessToken, request);
            return ResponseEntity.ok(responseService.success("닉네임이 성공적으로 변경되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(responseService.fail(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "닉네임 변경 실패: " + e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> deleteUser(@RequestHeader("Authorization") String authorization) {
        String accessToken = extractTokenOrThrow(authorization);
        try {
            userService.deleteUser(accessToken);

            return ResponseEntity.ok(responseService.success("회원 탈퇴가 완료되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseService.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "회원 탈퇴 실패: " + e.getMessage()));
        }
    }

    private String extractTokenOrThrow(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Authorization 헤더가 유효하지 않습니다.");
    }
    
}

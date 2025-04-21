package com.example.demo.user.controller;

import com.example.demo.auth.dto.*;

import com.example.demo.common.dto.ApiResponse;
import com.example.demo.config.jwt.JwtAuthentication;
import com.example.demo.user.dto.UserRegisterRequest;
import com.example.demo.user.dto.UpdateNicknameRequest;
import com.example.demo.user.entity.User;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/change-nickname")
    public ResponseEntity<ApiResponse<Void>> changeNickname(
            @AuthenticationPrincipal JwtAuthentication authentication,
            @RequestBody ChangeNicknameRequest request) {
        try {
            userService.changeNickname(authentication.getEmail(), request.getNewNickname());
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "닉네임이 성공적으로 변경되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "닉네임 변경 실패: " + e.getMessage(),
                            null));
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<User>> getUserInfo(@AuthenticationPrincipal JwtAuthentication authentication) {
        try {
            User user = userService.getUserInfo(authentication.getEmail());
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "회원 정보 조회 성공", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "회원 정보 조회 실패: " + e.getMessage(),
                            null));
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal JwtAuthentication authentication) {
        try {
            userService.deleteUser(authentication.getEmail());

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "회원 탈퇴가 완료되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "회원 탈퇴 실패: " + e.getMessage(),
                            null));
        }
    }
}

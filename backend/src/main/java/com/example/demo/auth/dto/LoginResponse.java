package com.example.demo.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private int status;
    private String message;
    private TokenData json;

    @Getter
    @Builder
    public static class TokenData {
        private String accessToken;
        private String refreshToken;
    }
}

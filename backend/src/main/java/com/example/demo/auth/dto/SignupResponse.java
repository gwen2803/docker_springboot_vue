package com.example.demo.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {
    private int status;
    private String message;
    private Object json; // 회원가입에서는 일반적으로 null
}

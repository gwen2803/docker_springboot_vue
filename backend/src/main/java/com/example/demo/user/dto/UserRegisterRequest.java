package com.example.demo.user.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String email;
    private String password;
    private String nickname;
}

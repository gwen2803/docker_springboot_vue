package com.example.demo.auth.dto;

import com.example.demo.user.entity.User;
import com.example.demo.user.entity.Token;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class SignupResponse {
    private User user;
    private Token token;
}

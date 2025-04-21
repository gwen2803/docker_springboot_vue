package com.example.demo.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class ChangeNicknameRequest {
    private String newNickname;
}

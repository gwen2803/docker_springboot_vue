package com.example.demo.common.service;

import com.example.demo.common.dto.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    public ApiResponse<?> success(String message, Object data) {
        return new ApiResponse<>(200, message, data);
    }

    public ApiResponse<?> fail(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}

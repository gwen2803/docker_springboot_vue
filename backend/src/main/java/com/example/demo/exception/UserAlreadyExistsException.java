package com.example.demo.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("이미 존재하는 사용자입니다.");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

package com.example.demo.user.repository;

import com.example.demo.user.entity.Token;
import com.example.demo.user.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByEmail(String email);

    void deleteByRefreshToken(String refreshToken);

    List<Token> findAllByEmail(String email);
}

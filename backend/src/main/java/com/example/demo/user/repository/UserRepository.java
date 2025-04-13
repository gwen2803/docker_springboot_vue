package com.example.demo.user.repository;

import com.example.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // 삭제되지 않은 사용자만 조회
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    // 존재 여부 확인 (삭제되지 않은 사용자)
    boolean existsByEmailAndDeletedAtIsNull(String email);
}

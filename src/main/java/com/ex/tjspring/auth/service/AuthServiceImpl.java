package com.ex.tjspring.auth.service;

import com.ex.tjspring.user.mapper.UserMapper;
import com.ex.tjspring.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    // 비밀번호 BCrypt 암호화를 위한 인코더 주입
    private final PasswordEncoder passwordEncoder;

    @Override
    public User login(String userId, String password) {
        // 1. userId로 사용자 찾기
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            return null;
        }
        // 2. BCrypt로 비밀번호 검증 - 평문과 해시 비교
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        return user;

    }
}

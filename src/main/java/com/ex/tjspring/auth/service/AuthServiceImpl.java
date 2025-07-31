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
        // userId로 사용자 찾기
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            return null;
        }
        String storedPassword = user.getPassword();

        // 1. BCrypt 해시 여부 판단
        boolean isBcrypt = storedPassword != null
                && storedPassword.startsWith("$2")
                && storedPassword.length() == 60;

        if (isBcrypt) {
            // 해시된 경우: matches() 사용
            if (!passwordEncoder.matches(password, storedPassword)) {
                return null;
            }
        } else {
            // 해시 안 된 경우: 평문 비교
            if (!password.equals(storedPassword)) {
                return null;
            }

            // 로그인 성공 시 -> 비밀번호를 BCrypt로 업데이트 (선택 사항)
//            String encoded = passwordEncoder.encode(password);
//            user.setPassword(encoded);
//            userMapper.updatePassword(user);
        }

        return user;

    }
}

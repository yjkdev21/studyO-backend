package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserUpdateRequest;
import com.ex.tjspring.user.mapper.UserMapper;
import com.ex.tjspring.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEditServiceImpl implements UserEditService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean updateUserInfo(UserUpdateRequest dto) {
        try {
            // 기존 사용자 조회
            User existingUser = userMapper.findById(dto.getId());
            if (existingUser == null) {
                return false;
            }

            // 기존 사용자 객체를 그대로 사용하고 변경할 필드만 업데이트
            existingUser.setNickname(dto.getNickname());
            existingUser.setIntroduction(dto.getIntroduction());
            existingUser.setProfileImage(dto.getProfileImage());

            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            userMapper.updateUser(existingUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
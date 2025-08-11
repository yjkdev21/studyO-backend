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
            User existingUser = userMapper.findById(dto.getId());
            if (existingUser == null) {
                return false;
            }

            existingUser.setNickname(dto.getNickname());
            existingUser.setIntroduction(dto.getIntroduction());

            // profileImage가 비어있거나 null이면 기존값 유지
            if (dto.getProfileImage() != null && !dto.getProfileImage().trim().isEmpty()) {
                existingUser.setProfileImage(dto.getProfileImage());
            }

            // password도 마찬가지
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

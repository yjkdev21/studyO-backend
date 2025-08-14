package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserUpdateRequest;
import com.ex.tjspring.user.mapper.UserMapper;
import com.ex.tjspring.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserEditServiceImpl implements UserEditService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean updateUserInfo(UserUpdateRequest dto) {
        try {
            User existingUser = userMapper.findById(dto.getId());
            if (existingUser == null) return false;

            existingUser.setNickname(dto.getNickname());
            existingUser.setIntroduction(dto.getIntroduction());

            if (dto.getProfileImage() != null && !dto.getProfileImage().trim().isEmpty()) {
                existingUser.setProfileImage(dto.getProfileImage());
            }
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            userMapper.updateUser(existingUser);
            return true;
        } catch (Exception e) {
            log.error("사용자 정보 수정 실패", e);
            return false;
        }
    }

    @Override
    public boolean updateUserInfoWithImage(UserUpdateRequest dto) {
        try {
            User existingUser = userMapper.findById(dto.getId());
            if (existingUser == null) return false;

            existingUser.setNickname(dto.getNickname());
            existingUser.setIntroduction(dto.getIntroduction());

            if (dto.getProfileImage() != null) {
                existingUser.setProfileImage(dto.getProfileImage());
            }
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            userMapper.updateUser(existingUser);
            log.info("프로필 수정 완료 - ID: {}, 닉네임: {}", dto.getId(), dto.getNickname());
            return true;
        } catch (Exception e) {
            log.error("프로필 수정 실패", e);
            return false;
        }
    }

    @Override
    public UserUpdateRequest getUserProfile(Long userId) {
        try {
            User user = userMapper.findById(userId);
            if (user == null) return null;

            UserUpdateRequest dto = new UserUpdateRequest();
            dto.setId(user.getId());
            dto.setNickname(user.getNickname());
            dto.setIntroduction(user.getIntroduction());
            dto.setProfileImage(user.getProfileImage()); // 비밀번호는 반환하지 않음

            return dto;
        } catch (Exception e) {
            log.error("프로필 조회 실패", e);
            return null;
        }
    }
}

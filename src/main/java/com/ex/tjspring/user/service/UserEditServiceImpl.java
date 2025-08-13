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
            if (existingUser == null) {
                return false;
            }

            // 기존 로직 유지 - 이미지는 URL로 직접 처리
            existingUser.setNickname(dto.getNickname());
            existingUser.setIntroduction(dto.getIntroduction());

            // profileImage가 비어있거나 null이면 기존값 유지 (기존 로직)
            if (dto.getProfileImage() != null && !dto.getProfileImage().trim().isEmpty()) {
                existingUser.setProfileImage(dto.getProfileImage());
            }

            // password도 마찬가지 (기존 로직)
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

    @Override
    public boolean updateUserInfoWithImage(UserUpdateRequest dto) {
        try {
            User existingUser = userMapper.findById(dto.getId());
            if (existingUser == null) {
                return false;
            }

            existingUser.setNickname(dto.getNickname());
            existingUser.setIntroduction(dto.getIntroduction());

            // 새로운 이미지 처리 로직 (파일 업로드용)
            if (dto.getProfileImage() != null) {
                existingUser.setProfileImage(dto.getProfileImage());
            }

            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            userMapper.updateUser(existingUser);
            log.info("사용자 프로필 수정 완료 - ID: {}, 닉네임: {}", dto.getId(), dto.getNickname());
            return true;

        } catch (Exception e) {
            log.error("사용자 프로필 수정 중 오류: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public UserUpdateRequest getUserProfile(Long userId) {
        try {
            User user = userMapper.findById(userId);
            if (user == null) {
                return null;
            }

            UserUpdateRequest dto = new UserUpdateRequest();
            dto.setId(user.getId());
            dto.setNickname(user.getNickname());
            dto.setIntroduction(user.getIntroduction());
            dto.setProfileImage(user.getProfileImage());
            // password는 보안상 반환하지 않음

            return dto;

        } catch (Exception e) {
            log.error("사용자 프로필 조회 중 오류: {}", e.getMessage(), e);
            return null;
        }
    }
}
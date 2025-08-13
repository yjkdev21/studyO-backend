package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserUpdateRequest;

public interface UserEditService {
    boolean updateUserInfo(UserUpdateRequest dto);
    boolean updateUserInfoWithImage(UserUpdateRequest dto);  // 새로 추가 (이미지 포함)
    UserUpdateRequest getUserProfile(Long userId);  // 새로 추가
}
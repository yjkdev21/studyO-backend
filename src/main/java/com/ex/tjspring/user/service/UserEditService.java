package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserUpdateRequest;

public interface UserEditService {

    boolean updateUserInfo(UserUpdateRequest dto); // 사용자 정보 수정

    boolean updateUserInfoWithImage(UserUpdateRequest dto); // 이미지 포함 사용자 정보 수정

    UserUpdateRequest getUserProfile(Long userId); // 사용자 프로필 조회
}

package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserUpdateRequest;

public interface UserEditService {
    boolean updateUserInfo(UserUpdateRequest dto);
}

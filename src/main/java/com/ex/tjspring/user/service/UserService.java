package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserRegisterRequest;
import com.ex.tjspring.user.model.User;

public interface UserService {
    User findById(Long id);
    User findByUserId(String userId);
    User findByEmail(String email);
    User findByNickname(String nickname);

    boolean isUserIdExists(String userId);
    boolean isNicknameExists(String nickname);
	boolean isEmailExists(String email);
    String registerUser(UserRegisterRequest request);
}

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

    // 아이디 찾기 - 이메일로 아이디 조회
    String findUserIdByEmail(String email);
    // 비밀번호 찾기 - 아이디와 이메일로 조회 후 비밀번호 변경
    boolean resetPassword(String userId, String email, String newPassword);
    // 회원 탈퇴 메서드
    boolean deleteUser(String userId);
}

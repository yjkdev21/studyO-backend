package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserRegisterRequest;

public interface UserService {
    boolean isUserIdExists(String userId);
    boolean isNicknameExists(String nickname);
	boolean isEmailExists(String email);
    String registerUser(UserRegisterRequest request);

    // 이메일로 아이디 찾기 - 탈퇴한 회원 제외
    String getActiveUserId(String email);

    // 비밀번호 변경
    boolean resetPassword(Long id, String newPassword);

    // 계정 검증
    boolean verifyUserAccount(String userId, String email);

    // 회원 탈퇴 메서드
    boolean deleteUser(String userId);

    boolean verifyPassword(String userId, String rawPassword);
}

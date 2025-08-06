package com.ex.tjspring.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private Long id;              // 사용자 고유 ID
    private String nickname;      // 수정할 닉네임
    private String password;      // 수정할 비밀번호 (선택)
    private String introduction;  // 자기소개
    private String profileImage;  // 프로필 이미지 URL
}

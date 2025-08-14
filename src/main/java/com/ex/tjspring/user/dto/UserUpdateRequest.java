package com.ex.tjspring.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private Long id; // 사용자 ID

    @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다.")
    private String nickname; // 닉네임

    private String password; // 비밀번호 (선택)

    @Size(max = 500, message = "자기소개는 500자를 초과할 수 없습니다.")
    private String introduction; // 자기소개

    private String profileImage; // S3 파일명
    private String profileImageFullPath; // 프로필 이미지 전체 URL
}

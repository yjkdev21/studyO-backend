package com.ex.tjspring.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString(exclude = "password") // 비밀번호 ToString 방지
public class User {
    private Long id;
    private String userId;

    @JsonIgnore
    private String password;

    private String email;
    private String nickname;
    private Timestamp createdAt;   // Date -> Timestamp 로 변경 가능

    private String profileImage;
    private String introduction;

    private String userLevel;      // 새로 추가된 컬럼
}

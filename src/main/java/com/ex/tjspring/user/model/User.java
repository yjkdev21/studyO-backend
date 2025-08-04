package com.ex.tjspring.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
// 비밀번호 ToString 방지 (보안용)
@ToString(exclude = "password")
public class User {
    private Long id;
    private String userId;
    // 비밀번호 Json 출력 방지 (보안용)
    @JsonIgnore
    private String password;
    private String email;
    private String nickname;
    private String isDeleted;
    private Date createdAt;
    private String globalRole;
    private String profileImage;
    private String introduction;
}

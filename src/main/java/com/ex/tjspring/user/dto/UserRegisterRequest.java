package com.ex.tjspring.user.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private Long id;
    private String userId;
    private String password;
    private String email;
    private String nickname;
    private String isDeleted;     // "N"
    private String globalRole;    // "USER"
    private String profileImage;  // ""
    private String introduction;  // ""
}
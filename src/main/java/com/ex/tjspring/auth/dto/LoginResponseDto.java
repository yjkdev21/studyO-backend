package com.ex.tjspring.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String userId;
    private String email;
    private String nickname;
    private String profileImage;
    private String introduction;
    private String globalRole;
    private Date createdAt;
}

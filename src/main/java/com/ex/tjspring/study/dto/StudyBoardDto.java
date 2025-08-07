package com.ex.tjspring.study.dto;

import lombok.Data;

@Data
public class StudyBoardDto {
    private Long id;
    private Long userId;
    private Long groupId;
    private String dashboardPostTitle;
    private String dashboardPostText;
    private String isNotice;
    private String createdAt;
    private String updatedAt;
    private String isDeleted;

    private Long writerId; // 작성자 아이디
    private String writerNickname;    // 스터디 내 닉네임
    private String writerProfileImage; // 프로필 이미지 URL

}

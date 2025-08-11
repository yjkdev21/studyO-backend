package com.ex.tjspring.bookmark.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Bookmark {

    // 북마크 기본 정보
    private Long id;
    private Long userId;
    private Long groupId;
    private LocalDateTime createdAt;

    // 스터디 그룹 정보 (JOIN)
    private String category;
    private String groupName;
    private String groupIntroduction;
    private Long groupOwnerId;
    private String ownerNickname;  // 그룹장 닉네임 (MyPage에서 사용)
    private LocalDateTime studyCreatedAt;
    private Integer maxMembers;
    private String studyMode;
    private String region;
    private String contact;
    private String thumbnail;
    private String thumbnailFullPath;  // S3 전체 URL (추가)
}
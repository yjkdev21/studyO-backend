package com.ex.tjspring.mainsearch.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StudyPostDTO {
    private Long studyPostId;
    private String title;
    private String content;
    // getter, setter 추가
    private String mode;
    private Long groupId;
    private String groupName;        // 그룹 이름 조인 결과 포함
    private String category;         // 그룹 카테고리
    private String studyMode;
    private Date recruitEndDate;
    private Integer viewCount;
    private String hashTag;

}

package com.ex.tjspring.mainsearch.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StudyPostDTO {
    private Long studyPostId;
    private String title;
    private String content;
    private String mode;
    private Long groupId;
    private String groupName;
    private String category;
    private String studyMode;
    private Date recruitEndDate;
    private Integer viewCount;
    private String hashTag;

}

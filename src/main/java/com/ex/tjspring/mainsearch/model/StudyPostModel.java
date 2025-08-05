package com.ex.tjspring.mainsearch.model;

import lombok.Data;

import java.util.Date;

@Data
public class StudyPostModel {
    private Long studyPostId;         // STUDY_POST_ID
    private String title;             // TITLE
    private String content;           // CONTENT
    private Long groupId;             // GROUP_ID
    private Long authorId;            // AUTHOR_ID
    private String studyMode;         // STUDY_MODE
    private Date createdAt;           // CREATED_AT
    private Date recruitStartDate;   // RECRUIT_START_DATE
    private Date recruitEndDate;     // RECRUIT_END_DATE
    private Date updatedAt;           // UPDATED_AT
    private Integer viewCount;        // VIEW_COUNT
    private String hashTag;           // HASH_TAG

    // 조인된 스터디 그룹 정보 (선택적)
    private String groupName;
    private String category;
    private Integer maxMembers;
    private String region;
    private String contact;
    private String groupIntroduction;
    private Long groupOwnerId;
    private Date groupCreatedAt;
    private String thumbnail;
    private String authorName; // 작성자 닉네임
}

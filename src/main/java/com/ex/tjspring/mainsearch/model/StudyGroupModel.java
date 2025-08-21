// StudyGroupModel.java
package com.ex.tjspring.mainsearch.model;

import lombok.Data;

import java.util.Date;

@Data
public class StudyGroupModel {
    private Long groupId;             // GROUP_ID
    private String groupName;         // GROUP_NAME
    private String category;          // CATEGORY
    private Integer maxMembers;       // MAX_MEMBERS
    private String studyMode;         // STUDY_MODE
    private String region;            // REGION
    private String contact;           // CONTACT
    private String groupIntroduction; // GROUP_INTRODUCTION
    private Long groupOwnerId;        // GROUP_OWNER_ID
    private Date createdAt;           // CREATED_AT
    private String thumbnail;         // THUMBNAIL

    private Integer viewCount;        // VIEW_COUNT
    private Integer bookmarkCount;    // BOOKMARK_COUNT
}

// src/main/java/com/ex/tjspring/admin/model/StudyGroupModel.java
package com.ex.tjspring.admin.model;

import lombok.Data;
import java.util.Date;

@Data
public class StudyGroupModel {
    private Long groupId;
    private String groupName;
    private String category;
    private Long maxMembers;
    private String studyMode;
    private String region;
    private String contact;
    private String groupIntroduction;
    private Long groupOwnerId;
    private Date createdAt;
    private String thumbnail;
    private Long currentMembers; // 현재 멤버 수
    private String status; // 스터디 상태
}
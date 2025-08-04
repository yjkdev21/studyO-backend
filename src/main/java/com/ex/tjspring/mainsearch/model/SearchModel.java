package com.ex.tjspring.mainsearch.model;

import lombok.Data;

import java.util.Date;

@Data
public class SearchModel {
    private Long id;
    private String groupName;
    private String category;
    private Integer maxMembers;
    private String studyMode;
    private String region;
    private String contact;
    private String groupIntroduction;
    private Date recruitEndDate;
    private Date studyStartDate;
    private Date studyEndDate;
    private Long groupOwnerId;
    private Date createdAt;
}

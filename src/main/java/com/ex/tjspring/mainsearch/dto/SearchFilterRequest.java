package com.ex.tjspring.mainsearch.dto;

import lombok.Data;

@Data
public class SearchFilterRequest {
    private String category;      // CATEGORY
    private String studyMode;     // STUDY_MODE ('ONLINE', 'OFFLINE')
    private String mode;          // 프론트엔드에서 보내는 mode 파라미터 (studyMode와 동일한 용도)
    private String region;        // REGION
    private Integer minMembers;   // MIN(MAX_MEMBERS)
    private Integer maxMembers;   // MAX(MAX_MEMBERS)
    private String search;        // 검색어 (GROUP_NAME, GROUP_INTRODUCTION)
    private Integer recruitingOnly; // 모집중만 보기 (1: 모집중, 0: 전체)
}
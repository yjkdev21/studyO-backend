package com.ex.tjspring.mainsearch.dto;

import lombok.Data;

@Data
public class BookmarkResponse {
    private Long studyGroupId;
    private int bookmarkCount;   // 북마크 개수
    private int viewCount;       // 조회수
}

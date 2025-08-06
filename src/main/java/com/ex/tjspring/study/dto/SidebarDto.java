package com.ex.tjspring.study.dto;

import lombok.Data;

@Data
public class SidebarDto {
    // study sidebar에 필요한 것들
    // 스터디 정보(카테고리, 스터디 이름, 연락방법)
    private Long id;
    private String name;
    private String contact;
    private String category;
}

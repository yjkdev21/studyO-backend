package com.ex.tjspring.mainsearch.dto;

import lombok.Data;

@Data
public class SearchDto {
    private String category;
    private String mode;
    private String region;
    private Integer recruitingOnly;  // 1=모집중만, 0=모집중상관없음
    private Integer minMembers;
    private Integer maxMembers;
    private String search;
}

package com.ex.tjspring.study.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class GroupDto {
    private Long id;
    private String groupName;
    private String category;
    private Long maxMembers;
    private String study_mode;
    private String region;
    private String contact;
    private String group_introduction;
    private String thumbnail;
    private Long group_owner_id;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime recruit_end_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime study_start_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime study_end_date;
    private LocalDateTime created_at;

}
package com.ex.tjspring.study.dto;

import lombok.Data;
import java.util.Date;
@Data
public class CalendarDto {
    private Long id;
    private Long groupId;
    private String title;
    private String content;
    private Date startDate;
    private Date endDate;
    private String bgColor;
    private String textColor;
    private Long writerId;
    private Date createdAt;
    private Date updatedAt;
    private String deletedYn;
}

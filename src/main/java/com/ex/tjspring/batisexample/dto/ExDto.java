package com.ex.tjspring.batisexample.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExDto {
    private Long id;
    private String title;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime regDate;
}

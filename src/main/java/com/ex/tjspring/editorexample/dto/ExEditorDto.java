package com.ex.tjspring.editorexample.dto;

import com.ex.tjspring.common.dto.AttachFileDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExEditorDto {
    private Long id;
    private String title;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime regDate;
    private List<AttachFileDto> attachFile;
}

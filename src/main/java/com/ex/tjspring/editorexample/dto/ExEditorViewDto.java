package com.ex.tjspring.editorexample.dto;

import com.ex.tjspring.common.dto.AttachFileDto; // AttachFileDto 임포트
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExEditorViewDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private List<AttachFileDto> attachFile;
}

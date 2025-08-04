package com.ex.tjspring.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AttachFileDto {
    private Long id; // 첨부파일 고유 ID (ATTACHMENTS.id)
    private Long postId; // 참조하는 게시글의 ID (ATTACHMENTS.post_id)
    private String fileName; // 원본 파일명 (ATTACHMENTS.file_name)
    private String storedFileName; // 서버에 저장된 UUID 파일명 (ATTACHMENTS.stored_file_name)
    private Long fileSize; // 파일 크기 (바이트) (ATTACHMENTS.file_size)
    private String fileType; // 파일 MIME 타입 (ATTACHMENTS.file_type)
    private LocalDateTime regDate; // 첨부파일 등록일 (ATTACHMENTS.reg_date)
}

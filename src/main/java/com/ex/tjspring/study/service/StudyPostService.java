package com.ex.tjspring.study.service;

import com.ex.tjspring.common.dto.AttachFileDto;
import com.ex.tjspring.common.service.AttachFileService;
import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.common.service.S3Service;
import com.ex.tjspring.editorexample.dto.StudyPostInsertDto;
import com.ex.tjspring.editorexample.dto.StudyPostUpdateDto;
import com.ex.tjspring.study.dao.IStudyPostDao;
import com.ex.tjspring.study.dto.GroupNickDto;
import com.ex.tjspring.study.dto.StudyPostDto;
import com.ex.tjspring.study.dto.StudyPostViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Spring의 @Transactional 임포트
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter; // 더 이상 수동 파싱에 필요 없음
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyPostService {

    private final IStudyPostDao dao;
    private final AttachFileService attachFileService;
    private final S3Service s3Service;

    // 유저가 개설한 스터디그룹 리스트 조회
    public List<GroupNickDto> selectGroupsFindByUserId(Long userId) {
        return dao.selectGroupsFindByUserId(userId);
    }

    // 특정 스터디그룹 정보 조회
    public GroupNickDto selectGroupsFindByGroupId(Long groupId) {
        return dao.selectGroupsFindByGroupId(groupId);
    }

    // 특정 스터디그룹에 홍보 게시글이 존재하는지 확인
    public boolean existsPostByGroupId(Long groupId) {
        return dao.existsPostByGroupId(groupId) > 0;
    }

    // 스터디그룹 홍보글 상세 조회
    public StudyPostViewDto selectPostFindByGroupId(Long groupId) {
        return dao.selectPostFindByGroupId(groupId);
    }

    // 홍보글 insert (이 메서드는 STUDY_POST 테이블에만 삽입하는 역할로 유지)
    public Long insertPost(StudyPostDto dto) {
        dao.insertPost(dto);
        return dto.getStudyPostId();
    }

    /**
     * 게시글 등록 및 첨부파일 처리를 하나의 트랜잭션으로 묶는 메서드.
     */
    @Transactional
    public Long createPostWithAttachments(StudyPostInsertDto insertDto, List<MultipartFile> attachments) throws IOException {
        // StudyPostInsertDto를 StudyPostDto로 변환
        StudyPostDto postDto = new StudyPostDto();
        postDto.setTitle(insertDto.getTitle());
        postDto.setContent(insertDto.getContent());
        postDto.setGroupId(insertDto.getGroupId());
        postDto.setAuthorId(insertDto.getAuthorId());
        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setRecruitStartDate(insertDto.getRecruitStartDate());
        postDto.setRecruitEndDate(insertDto.getRecruitEndDate());
        postDto.setUpdatedAt(LocalDateTime.now());
        postDto.setViewCount(0);
        postDto.setHashTag(insertDto.getHashTag());

        // 1. STUDY_POST 테이블에 게시글 삽입
        dao.insertPost(postDto); // selectKey에 의해 postDto.studyPostId가 채워집니다.
        Long newPostId = postDto.getStudyPostId();

        // 2. 첨부파일 처리 (S3 업로드 및 ATTACHMENTS 테이블에 삽입)
        handleFileUpload(newPostId, attachments);

        return newPostId;
    }

    // 홍보글 수정 및 첨부파일 처리
    @Transactional
    public void updatePost(StudyPostUpdateDto updateDto) throws IOException {
        StudyPostDto dto = new StudyPostDto();
        dto.setStudyPostId(updateDto.getStudyPostId());
        dto.setTitle(updateDto.getTitle());
        dto.setContent(updateDto.getContent());
        dto.setAuthorId(updateDto.getAuthorId());

        // DTO 필드가 @DateTimeFormat에 의해 이미 LocalDateTime으로 변환되어 넘어옵니다.
        // 따라서 여기서 수동 파싱 로직은 필요 없습니다.
        dto.setRecruitStartDate(updateDto.getRecruitStartDate());
        dto.setRecruitEndDate(updateDto.getRecruitEndDate());

        dto.setUpdatedAt(LocalDateTime.now());
        dto.setHashTag(updateDto.getHashTag());

        dao.updatePost(dto);

        // 삭제된 첨부파일 처리
        deleteAttachFiles(updateDto.getDeletedStoredFileNames());

        // 새로 추가된 첨부파일 처리
        handleFileUpload(updateDto.getStudyPostId(), updateDto.getNewAttachments());
    }

    // 홍보글 삭제 (게시글 및 관련 첨부파일 모두 삭제)
    @Transactional
    public void deletePost(Long studyPostId) {

        StudyPostViewDto viewDto = dao.selectPostFindByPostId(studyPostId);
        if (viewDto != null) {
            deletePostAttachFiles(viewDto); // S3 및 DB 메타데이터 삭제
        }
        dao.deletePost(studyPostId); // 게시글 삭제
    }

    // 첨부파일 추가 (S3 upload 및 DB 저장)
    public void handleFileUpload(Long postId, List<MultipartFile> attachments) throws IOException {
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String storedFileName = s3Service.upload(S3DirKey.ATTACHFILE, file);

                    AttachFileDto attachFileDto = new AttachFileDto();
                    attachFileDto.setPostId(postId);
                    attachFileDto.setFileName(originalFileName);
                    attachFileDto.setStoredFileName(storedFileName);
                    attachFileDto.setFileSize(file.getSize());
                    attachFileDto.setFileType(file.getContentType());
                    attachFileDto.setRegDate(LocalDateTime.now());

                    attachFileService.insert(attachFileDto);
                }
            }
        }
    }

    // 수정 시 개별 첨부파일 삭제 (S3 및 DB)
    public void deleteAttachFiles(List<String> deletedStoredFileNames) {
        if (deletedStoredFileNames != null && !deletedStoredFileNames.isEmpty()) {
            for (String storedFileName : deletedStoredFileNames) {
                try {
                    s3Service.delete(S3DirKey.ATTACHFILE, storedFileName);
                    attachFileService.deleteByStoredFileName(storedFileName);
                } catch (Exception e) {
                    System.err.println("S3/DB에서 파일 " + storedFileName + " 삭제 실패: " + e.getMessage());
                }
            }
        }
    }

    // 홍보글 삭제 시 해당 포스트의 첨부파일 일괄 삭제 (S3 및 DB)
    public void deletePostAttachFiles(StudyPostViewDto dto) {
        if (dto != null && dto.getAttachFile() != null) {
            for (AttachFileDto file : dto.getAttachFile()) {
                try {
                    s3Service.delete(S3DirKey.ATTACHFILE, file.getStoredFileName());
                    attachFileService.deleteByStoredFileName(file.getStoredFileName());
                } catch (Exception e) {
                    System.err.println("게시글 " + dto.getStudyPostId() + "에 연결된 S3 파일 " + file.getStoredFileName() + " 삭제 실패: " + e.getMessage());
                }
            }
        }
    }
}
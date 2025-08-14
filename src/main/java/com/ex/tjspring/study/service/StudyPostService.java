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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyPostService {

    private final IStudyPostDao dao;
    private final S3Service s3Service;
    private final AttachFileService attachFileService;

    // 유저가 개설한 스터디그룹 리스트
    public List<GroupNickDto> selectGroupsFindByUserId(Long userId) {
        return dao.selectGroupsFindByUserId(userId);
    }

    public GroupNickDto selectGroupsFindByGroupId(Long groupId) {
        return dao.selectGroupsFindByGroupId(groupId);
    }

    public boolean existsPostByGroupId(Long groupId) {
        return dao.existsPostByGroupId(groupId) > 0;
    }

    // 스터디그룹 홍보글 가져오기
    public StudyPostViewDto selectPostFindByGroupId(Long groupId) {
        return dao.selectPostFindByGroupId(groupId);
    }


    @Transactional
    public Long insertPostWithFiles(StudyPostInsertDto insertDto) throws IOException {
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

        // MyBatis가 insertPost 호출 후 postDto 객체의 studyPostId를 업데이트합니다.
        dao.insertPost(postDto);

        // 업데이트된 DTO에서 정확한 ID를 가져와 사용합니다.
        Long newPostId = postDto.getStudyPostId();
        log.info("#### Service에서 DTO로부터 가져온 newPostId: {}", newPostId);

        // 첨부파일 처리
        List<MultipartFile> attachments = insertDto.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            handleFileUpload(newPostId, attachments);
        }

        return newPostId;
    }

    /**
     * 홍보글 수정 및 첨부파일 업데이트를 트랜잭션으로 묶습니다.
     * @param updateDto 수정 정보
     * @throws IOException
     */
    @Transactional
    public void updatePostWithFiles(StudyPostUpdateDto updateDto) throws IOException {
        StudyPostDto postDto = new StudyPostDto();
        postDto.setStudyPostId(updateDto.getStudyPostId());
        postDto.setTitle(updateDto.getTitle());
        postDto.setContent(updateDto.getContent());
        postDto.setAuthorId(updateDto.getAuthorId());
        postDto.setRecruitStartDate(updateDto.getRecruitStartDate());
        postDto.setRecruitEndDate(updateDto.getRecruitEndDate());
        postDto.setUpdatedAt(LocalDateTime.now());
        postDto.setHashTag(updateDto.getHashTag());

        // 게시글 업데이트
        dao.updatePost(postDto);

        // 삭제된 파일 처리
        deleteAttachFiles(updateDto.getDeletedStoredFileNames());

        // 새로 추가된 파일 처리
        List<MultipartFile> newAttachments = updateDto.getNewAttachments();
        if (newAttachments != null && !newAttachments.isEmpty()) {
            handleFileUpload(updateDto.getStudyPostId(), newAttachments);
        }
    }

    /**
     * 홍보글 삭제 및 관련 파일 삭제를 트랜잭션으로 묶습니다.
     * @param viewDto 삭제할 게시글 정보
     */
    @Transactional
    public void deletePostWithFiles(StudyPostViewDto viewDto) {
        if (viewDto != null && viewDto.getAttachFile() != null) {
            // S3에서 파일 삭제
            for (AttachFileDto file : viewDto.getAttachFile()) {
                try {
                    s3Service.delete(S3DirKey.ATTACHFILE, file.getStoredFileName());
                    log.info("S3 파일 삭제 완료: {}", file.getStoredFileName());
                } catch (Exception e) {
                    log.error("S3 파일 삭제 실패: {}", file.getStoredFileName(), e);
                    // 파일 하나 삭제 실패해도 트랜잭션은 롤백하지 않고 계속 진행
                }
            }
        }
        // 게시글 삭제 (첨부파일 메타데이터는 FK cascade에 의해 자동 삭제됩니다.)
        dao.deletePost(viewDto.getStudyPostId());
    }


    public boolean increasePostViewCnt(Long StudyPostId) {
        return dao.increasePostViewCnt(StudyPostId) > 0;
    }

    // 홍보글 insert
    // 이 메서드는 이제 사용되지 않고, insertPostWithFiles()로 대체됩니다.
    public Long insertPost(StudyPostDto dto) {
        return dao.insertPost(dto);
    }

    // 홍보글 update
    // 이 메서드는 이제 사용되지 않고, updatePostWithFiles()로 대체됩니다.
    public void updatePost(StudyPostDto dto) {
        dao.updatePost(dto);
    }

    // 홍보글 delete
    // 이 메서드는 이제 사용되지 않고, deletePostWithFiles()로 대체됩니다.
    public void deletePost(Long studyPostId) {
        dao.deletePost(studyPostId);
    }


    public int selectFindByGroupIdBookmarkCnt(Long groupId) {
        return dao.selectFindByGroupIdBookmarkCnt(groupId);
    }

    // 첨부파일 추가 S3 upload
    private void handleFileUpload(Long postId, List<MultipartFile> attachments) throws IOException {
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

    // 수정시 attachFile 개별 삭제
    private void deleteAttachFiles(List<String> deletedFileNames) {
        if (deletedFileNames != null && !deletedFileNames.isEmpty()) {
            for (String storedFileName : deletedFileNames) {
                try {
                    s3Service.delete(S3DirKey.ATTACHFILE, storedFileName);
                    attachFileService.deleteByStoredFileName(storedFileName);
                    log.info("S3 및 DB에서 파일 삭제 완료: {}", storedFileName);
                } catch (Exception e) {
                    log.error("S3/DB에서 파일 {} 삭제 실패: {}", storedFileName, e.getMessage(), e);
                }
            }
        }
    }
}

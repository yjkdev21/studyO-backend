package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.StudyBoardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudyBoardDao {
    // 전체 글 조회
    List<StudyBoardDto> selectAllByGroupId(Long groupId);

    // 공지 조회
    List<StudyBoardDto> selectNoticeByGroupId(Long groupId);

    // 일반글 조회
    List<StudyBoardDto> selectNormalByGroupId(Long groupId);

    // 상세 조회
    StudyBoardDto selectPostById(Long id);

    // 등록
    void insertPost(StudyBoardDto dto);

    // 수정
    void updatePost(StudyBoardDto dto);

    // 삭제 (글id + 사용자 id → 내가 작성한 글만 삭제 가능)
    void deletePost(@Param("id") Long id, @Param("userId") Long userId);
}

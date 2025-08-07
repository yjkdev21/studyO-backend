package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.GroupNickDto;
import com.ex.tjspring.study.dto.StudyPostDto;
import com.ex.tjspring.study.dto.StudyPostViewDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStudyPostDao {

    // userId 로 스터디 그룹정보 가져오기
    List<GroupNickDto> selectGroupsFindByUserId(Long userId);

    // groudId 로 스터디 그룹정보 가져오기
    GroupNickDto selectGroupsFindByGroupId(Long groupId);
    // ----- post -----

    int existsPostByGroupId(Long groupId);

    StudyPostViewDto selectPostFindByGroupId(Long groudId);

    StudyPostViewDto selectPostFindByPostId(Long postId);

    Long insertPost(StudyPostDto dto);

    void updatePost(StudyPostDto dto);

    void deletePost(Long studyPostId);

}

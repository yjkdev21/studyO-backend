package com.ex.tjspring.study.service;


import com.ex.tjspring.study.dao.IStudyPostDao;
import com.ex.tjspring.study.dto.GroupNickDto;
import com.ex.tjspring.study.dto.StudyPostDto;
import com.ex.tjspring.study.dto.StudyPostViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyPostService {

    private final IStudyPostDao dao;

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

    // 홍보글 insert
    public Long insertPost(StudyPostDto dto) {
        return dao.insertPost(dto);
    }

    // 홍보글 update
    public void updatePost(StudyPostDto dto) {
        dao.updatePost(dto);
    }

    // 홍보글 delete
    public void deletePost(Long studyPostId) {
        dao.deletePost(studyPostId);
    }


}

package com.ex.tjspring.study.service;

import com.ex.tjspring.study.dao.GroupDao;
import com.ex.tjspring.study.dao.StudyBoardDao;
import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.dto.StudyBoardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyBoardServiceImpl {
    @Autowired
    private StudyBoardDao studyBoardDao;

    @Autowired
    private GroupDao groupDao;


    public GroupDto getGroupInfo(Long groupId) {
        return groupDao.selectGroupById(groupId);
    }

    // 전체 조회
    public List<StudyBoardDto> getAllPosts(Long groupId) {
        return studyBoardDao.selectAllByGroupId(groupId);
    }

    // 공지만 조회
    public List<StudyBoardDto> getAllNotice(Long groupId) {
        return studyBoardDao.selectNoticeByGroupId(groupId);
    }

    // 일반글 조회
    public List<StudyBoardDto> getAllNormalPosts(Long groupId) {
        return studyBoardDao.selectNormalByGroupId(groupId);
    }

    // 상세 조회
    public  StudyBoardDto getSelectPostById(Long id) {
        return studyBoardDao.selectPostById(id);
    }
    // 글 등록
    public  void insertPost(StudyBoardDto dto) {
        studyBoardDao.insertPost(dto);
    }

    // 수정
    public  void updatePost(StudyBoardDto dto) {
        studyBoardDao.updatePost(dto);
    }

    // 삭제
    public void deletePost(Long id, Long userId) {
    studyBoardDao.deletePost(id, userId);
    }
}

package com.ex.tjspring.study.controller;

import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.dto.StudyBoardDto;
import com.ex.tjspring.study.service.StudyBoardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study/board") // 공통 url 경로 성정
public class StudyBoardController {

    @Autowired
    private StudyBoardServiceImpl studyBoardService;

    // 스터디그룹 정보
    @GetMapping("/group/{groupId}/info")
    public GroupDto getGroupInfo(@PathVariable Long groupId) {
        return studyBoardService.getGroupInfo(groupId);
    }

    // 전체 조회
    @GetMapping("/group/{groupId}")
    public List<StudyBoardDto> getAllPosts(@PathVariable Long groupId) {
        return studyBoardService.getAllPosts(groupId);
    }

    // 공지 조회
    @GetMapping("/group/{groupId}/notice")
    public List<StudyBoardDto> getAllNotice(@PathVariable Long groupId) {
        return studyBoardService.getAllNotice(groupId);
    }

    // 일반글 조회
    @GetMapping("/group/{groupId}/normal")
    public List<StudyBoardDto> getAllNormalPosts(@PathVariable Long groupId) {
        return studyBoardService.getAllNormalPosts(groupId);
    }

    // 상세 조회
    @GetMapping("/{id}")
    public StudyBoardDto getPostsDetail(@PathVariable Long id) {
        return studyBoardService.getSelectPostById(id);
    }

    // 글 등록
    @PostMapping
    public  void insertPost(@RequestBody StudyBoardDto dto) {
        studyBoardService.insertPost(dto);
    }

    // 수정
    @PutMapping
    public  void updatePost(@RequestBody StudyBoardDto dto) {
        studyBoardService.updatePost(dto);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id,
                           @RequestHeader("X-USER-ID") Long userId) {
        studyBoardService.deletePost(id, userId);
    }
}

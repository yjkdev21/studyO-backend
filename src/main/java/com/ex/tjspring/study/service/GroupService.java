package com.ex.tjspring.study.service;

import com.ex.tjspring.study.dao.GroupDao;
import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.dto.GroupEditDto;
import com.ex.tjspring.study.dto.GroupInsertDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class GroupService {

    @Autowired
    private GroupDao dao;
    public void insertGroup(GroupInsertDto groupInsertDto) {
        log.info("그룹 생성 시작: {}", groupInsertDto.getGroupName());

        // 중복 체크
        if (existsByGroupName(groupInsertDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        if (existsByNickName(groupInsertDto.getNickName())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        dao.insertGroup(groupInsertDto);
        log.info("그룹 생성 완료: {}", groupInsertDto.getGroupName());
    }

    // 전체 그룹 조회
    public List<GroupDto> selectAllGroups() {
        log.info("전체 그룹 조회");
        return dao.selectAllGroups();
    }

    // 특정 그룹 조회
    public GroupDto selectGroupById(Long id) {
        log.info("그룹 조회 - ID: {}", id);
        GroupDto group = dao.selectGroupById(id);
        if (group == null) {
            throw new IllegalArgumentException("존재하지 않는 그룹입니다. ID: " + id);
        }
        return group;
    }

    // 그룹 수정
    public void updateGroup(GroupEditDto groupEditDto) {
        log.info("그룹 수정 시작 - ID: {}", groupEditDto.getId());

        // 존재하는 그룹인지 확인
        GroupDto existingGroup = selectGroupById(groupEditDto.getId());

        // 그룹명이 변경되었다면 중복 체크
        if (!existingGroup.getGroupName().equals(groupEditDto.getGroupName())
                && existsByGroupName(groupEditDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        // 닉네임이 변경되었다면 중복 체크
        if (!existingGroup.getNickName().equals(groupEditDto.getNickName())
                && existsByNickName(groupEditDto.getNickName())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        dao.updateGroup(groupEditDto);
        log.info("그룹 수정 완료 - ID: {}", groupEditDto.getId());
    }

    public void deleteGroup(Long id) {
        log.info("그룹 삭제 시작 - ID: {}", id);

        selectGroupById(id);

        dao.deleteGroup(id);
        log.info("그룹 삭제 완료 - ID: {}", id);
    }

    // 그룹명 중복 확인
    public boolean existsByGroupName(String groupName) {
        return dao.existsByGroupName(groupName);
    }
    // 닉네임 중복 확인
    public boolean existsByNickName(String nickName) {
        return dao.existsByNickName(nickName);
    }

    //그룹 상태
    public void updateGroupStatus(Long id, String status) {
        log.info("그룹 상태 업데이트 - ID: {}, Status: {}", id, status);

        selectGroupById(id);

        dao.updateGroupStatus(id, status);
        log.info("그룹 상태 업데이트 완료 - ID: {}", id);
    }
}
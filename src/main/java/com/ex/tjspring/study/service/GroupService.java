package com.ex.tjspring.study.service;

import com.ex.tjspring.study.dao.GroupDao;
import com.ex.tjspring.study.dto.GroupDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class GroupService {

    @Autowired
    private GroupDao dao;

    public void insert(GroupDto groupDto) {
        log.info("그룹 생성 시작: {}", groupDto.getGroupName());

        // 중복 체크
        if (existsByGroupName(groupDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        dao.insert(groupDto);
    }

    public GroupDto selectGroupById(Long id) {
        return dao.selectGroupById(id);
    }

    // 전체 그룹 조회
    public List<GroupDto> selectAllGroups() {
        log.info("전체 그룹 조회");
        return dao.selectAllGroups();
    }

    // 그룹 수정
    public void update(GroupDto groupDto) {
        log.info("그룹 수정 시작 - ID: {}", groupDto.getGroupId());

        // 존재하는 그룹인지 확인
        GroupDto existingGroup = selectGroupById(groupDto.getGroupId());

        // 그룹명이 변경되었다면 중복 체크
        if (!existingGroup.getGroupName().equals(groupDto.getGroupName())
                && existsByGroupName(groupDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        dao.update(groupDto);
        log.info("그룹 수정 완료 - ID: {}", groupDto.getGroupId());
    }

    //그룹 삭제
    public void delete(Long id) {
        log.info("그룹 삭제 시작 - ID: {}", id);

        selectGroupById(id);

        dao.delete(id);
        log.info("그룹 삭제 완료 - ID: {}", id);
    }


//    // 그룹명 중복 확인
    public boolean existsByGroupName(String groupName) {
        return dao.existsByGroupName(groupName) > 0;
    }


}
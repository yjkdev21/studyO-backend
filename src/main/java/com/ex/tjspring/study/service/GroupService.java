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

    // ========== 기존 메서드들 (그대로 유지) ==========

    public void insert(GroupDto groupDto) {
        log.info("그룹 생성 시작: {}", groupDto.getGroupName());

        if (existsByGroupName(groupDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        dao.insert(groupDto);
    }

    public GroupDto selectGroupById(Long id) {
        return dao.selectGroupById(id);
    }

    public List<GroupDto> selectAllGroups() {
        log.info("전체 그룹 조회");
        return dao.selectAllGroups();
    }

    public void update(GroupDto groupDto) {
        log.info("그룹 수정 시작 - ID: {}", groupDto.getGroupId());

        GroupDto existingGroup = selectGroupById(groupDto.getGroupId());

        if (!existingGroup.getGroupName().equals(groupDto.getGroupName())
                && existsByGroupName(groupDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        dao.update(groupDto);
        log.info("그룹 수정 완료 - ID: {}", groupDto.getGroupId());
    }

    public void delete(Long id) {
        log.info("그룹 삭제 시작 - ID: {}", id);

        selectGroupById(id);

        dao.delete(id);
        log.info("그룹 삭제 완료 - ID: {}", id);
    }

    public boolean existsByGroupName(String groupName) {
        return dao.existsByGroupName(groupName) > 0;
    }

    // ========== 새로 추가되는 메서드들 ==========

    public List<GroupDto> getStudyGroupsByUserId(Long userId) {
        log.info("사용자 {}의 참여 그룹 조회", userId);
        return dao.findByUserId(userId);
    }

    public List<GroupDto> getActiveStudyGroupsByUserId(Long userId) {
        log.info("사용자 {}의 활성 그룹 조회", userId);
        return dao.findActiveByUserId(userId);
    }
}
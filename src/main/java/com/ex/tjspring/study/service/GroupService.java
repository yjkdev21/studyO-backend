package com.ex.tjspring.study.service;

import com.ex.tjspring.study.dao.GroupDao;
import com.ex.tjspring.study.dto.GroupDto;
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


    public String getUserNickname(Long userId) {
        log.info("사용자 {}의 닉네임 조회", userId);
        return dao.selectUserNickname(userId);
    }


    public void insert(GroupDto groupDto) {
        log.info("그룹 생성 시작: {}", groupDto.getGroupName());

        if (existsByGroupName(groupDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        dao.insert(groupDto);
    }

    public void insertWithMembership(GroupDto groupDto) {
        log.info("그룹 생성 및 멤버십 등록 시작: {}", groupDto.getGroupName());

        if (existsByGroupName(groupDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        // 1. 스터디 그룹 생성
        dao.insert(groupDto);
        log.info("그룹 생성 완료 - ID: {}", groupDto.getGroupId());

        // 2. 그룹 생성자를 멤버십에 자동 추가
        groupDto.setMemberRole("ADMIN");//admin으로 지정
        dao.insertMembership(groupDto);
        log.info("그룹 생성자 멤버십 등록 완료 - 그룹ID: {}, 사용자ID: {}, 닉네임: {}",
                groupDto.getGroupId(), groupDto.getGroupOwnerId(), groupDto.getNickname());
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

        if (existingGroup == null) {
            throw new IllegalArgumentException("존재하지 않는 그룹입니다.");
        }

        // 그룹명이 변경된 경우에만 중복 검사
        if (!existingGroup.getGroupName().equals(groupDto.getGroupName())
                && existsByGroupName(groupDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        // 1. 그룹 정보 업데이트
        dao.update(groupDto);
        log.info("그룹 정보 수정 완료 - ID: {}", groupDto.getGroupId());

        // 2. 닉네임이 있는 경우 멤버십 테이블의 닉네임도 업데이트
        if (groupDto.getNickname() != null && !groupDto.getNickname().trim().isEmpty()) {
            dao.updateNickname(groupDto);
            log.info("멤버십 닉네임 수정 완료 - 그룹ID: {}, 사용자ID: {}, 새 닉네임: {}",
                    groupDto.getGroupId(), groupDto.getGroupOwnerId(), groupDto.getNickname());
        }
    }

    public void delete(Long id) {
        log.info("그룹 삭제 시작 - ID: {}", id);

        selectGroupById(id);

        dao.delete(id);
        log.info("그룹 삭제 완료 - ID: {}", id);
    }

    public void deleteGroupIfOwnerAndNoMembers(Long groupId, Long userId) {
        GroupDto group = dao.selectGroupById(groupId);

        if (group == null) {
            throw new IllegalArgumentException("그룹이 존재하지 않습니다.");
        }

        if (!group.getGroupOwnerId().equals(userId)) {
            throw new IllegalStateException("방장만 스터디 그룹을 삭제할 수 있습니다.");
        }

        int memberCount = dao.countMembersByGroupId(groupId);  // 이 메서드를 GroupDao에 만들어야 함
        if (memberCount > 1) {
            throw new IllegalStateException("다른 멤버가 존재하여 삭제할 수 없습니다.");
        }

        // 그룹과 멤버십 삭제는 GroupDao에 있는 메서드 사용
        dao.delete(groupId);
        dao.deleteMembershipsByGroupId(groupId);  // 멤버십 전체 삭제 메서드도 만들어야 함
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
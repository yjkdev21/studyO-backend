package com.ex.tjspring.study.service;

import com.ex.tjspring.common.service.S3DirKey;
import com.ex.tjspring.common.service.S3Service;
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

    @Autowired
    private S3Service s3Service;

    // 썸네일 URL 처리 메서드 (private)
    private void processThumbnailUrl(GroupDto group) {
        String thumbnail = group.getThumbnail();
        if (thumbnail == null || thumbnail.isEmpty() || thumbnail.contains("default")) {
            group.setThumbnailFullPath("/images/default-thumbnail.png");
        } else {
            group.setThumbnailFullPath(s3Service.getFileFullPath(S3DirKey.STUDYGROUPIMG, thumbnail));
        }
    }

    // 여러 그룹의 썸네일 URL 처리 (private)
    private void processThumbnailUrls(List<GroupDto> groups) {
        if (groups != null) {
            for (GroupDto group : groups) {
                processThumbnailUrl(group);
            }
        }
    }

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

    @Transactional
    public Long insertWithMembership(GroupDto groupDto) {
        log.info("그룹 생성 및 멤버십 등록 시작: {}", groupDto.getGroupName());

        if (existsByGroupName(groupDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        // 1. 스터디 그룹 생성
        dao.insert(groupDto);
        log.info("그룹 생성 완료 - ID: {}", groupDto.getGroupId());

        // 2. 그룹 생성자를 멤버십에 자동 추가
        groupDto.setMemberRole("ADMIN"); // admin으로 지정
        dao.insertMembership(groupDto);
        log.info("그룹 생성자 멤버십 등록 완료 - 그룹ID: {}, 사용자ID: {}, 닉네임: {}",
                groupDto.getGroupId(), groupDto.getGroupOwnerId(), groupDto.getNickname());

        // 생성된 그룹 ID 반환
        return groupDto.getGroupId();
    }

    public GroupDto selectGroupById(Long id) {
        GroupDto group = dao.selectGroupById(id);
        if (group != null) {
            processThumbnailUrl(group);
        }
        return group;
    }

    public List<GroupDto> selectAllGroups() {
        log.info("전체 그룹 조회");
        List<GroupDto> groups = dao.selectAllGroups();
        processThumbnailUrls(groups);
        return groups;
    }

    public void update(GroupDto groupDto) {
        log.info("그룹 수정 시작 - ID: {}", groupDto.getGroupId());

        GroupDto existingGroup = dao.selectGroupById(groupDto.getGroupId()); // 썸네일 처리 없이 원본 데이터 조회

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

        GroupDto existingGroup = dao.selectGroupById(id); // 썸네일 처리 없이 원본 데이터 조회
        if (existingGroup == null) {
            throw new IllegalArgumentException("존재하지 않는 그룹입니다.");
        }

        dao.delete(id);
        log.info("그룹 삭제 완료 - ID: {}", id);
    }

    public boolean existsByGroupName(String groupName) {
        return dao.existsByGroupName(groupName) > 0;
    }

    // ========== 새로 추가되는 메서드들 ==========

    public List<GroupDto> getStudyGroupsByUserId(Long userId) {
        log.info("사용자 {}의 참여 그룹 조회", userId);
        List<GroupDto> groups = dao.findByUserId(userId);
        processThumbnailUrls(groups);
        return groups;
    }

    public List<GroupDto> getActiveStudyGroupsByUserId(Long userId) {
        log.info("사용자 {}의 활성 그룹 조회", userId);
        List<GroupDto> groups = dao.findActiveByUserId(userId);
        processThumbnailUrls(groups);
        return groups;
    }
}
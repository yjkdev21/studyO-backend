package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.GroupDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GroupDao {

    void insert(GroupDto groupDto);

    // 스터디 멤버십 등록 (그룹 생성자 자동 추가)
    void insertMembership(GroupDto groupDto);

    String selectUserNickname(@Param("userId") Long userId);

    List<GroupDto> selectAllGroups();

    void update(GroupDto groupDto);

    void updateNickname(GroupDto groupDto);

    void delete(Long id);
    void deleteMembershipsByGroupId(Long groupId);

    GroupDto selectGroupById(Long id);

    int existsByGroupName(String groupName);

    int countMembersByGroupId(@Param("groupId") Long groupId);

    // 새로 추가되는 메서드들
    List<GroupDto> findByUserId(@Param("userId") Long userId);

    List<GroupDto> findActiveByUserId(@Param("userId") Long userId);
}

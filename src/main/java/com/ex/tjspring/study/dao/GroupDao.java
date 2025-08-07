package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.GroupDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupDao {

    void insert(GroupDto groupDto);

    void insertMembership(GroupDto groupDto);

    String selectUserNickname(@Param("userId") Long userId);

    List<GroupDto> selectAllGroups();

    void update(GroupDto groupDto);

    void updateNickname(GroupDto groupDto);

    void delete(Long id);

    GroupDto selectGroupById(Long id);

    int existsByGroupName(String groupName);

    // 새로 추가되는 메서드들
    List<GroupDto> findByUserId(@Param("userId") Long userId);

    List<GroupDto> findActiveByUserId(@Param("userId") Long userId);

    // 멤버 수
    int countMembersByGroupId(@Param("groupId") Long groupId);

    //스터디 그룹 자체 삭제
    void deleteMembershipsByGroupId(@Param("groupId") Long groupId);


}
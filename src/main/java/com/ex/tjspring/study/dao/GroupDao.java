package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.dto.GroupEditDto;
import com.ex.tjspring.study.dto.GroupInsertDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupDao {

    void insertGroup(GroupInsertDto groupInsertDto);

    List<GroupDto> selectAllGroups();

    GroupDto selectGroupById(Long id);

    void updateGroup(GroupEditDto groupEditDto);

    void deleteGroup(Long id);

    // 그룹명 중복 확인
    boolean existsByGroupName(String groupName);
    // 닉네임 중복 확인
    boolean existsByNickName(String nickName);

    List<GroupDto> selectGroupsByCategory(String category);

    List<GroupDto> selectGroupsByRegion(String region);

    List<GroupDto> selectRecruitingGroups();

    List<GroupDto> selectGroupsByOwnerId(Long ownerId);

    void updateGroupStatus(Long id, String status);
}
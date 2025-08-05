package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.GroupDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupDao {

    void insert(GroupDto groupDto);

    List<GroupDto> selectAllGroups();

    void update(GroupDto groupDto);

    void delete(Long id);

    GroupDto selectGroupById(Long id);

    int existsByGroupName(String groupName);

    // 새로 추가되는 메서드들
    List<GroupDto> findByUserId(@Param("userId") Long userId);

    List<GroupDto> findActiveByUserId(@Param("userId") Long userId);
}
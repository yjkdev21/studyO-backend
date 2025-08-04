package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.GroupDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupDao {

    void insert(GroupDto groupDto);

    List<GroupDto> selectAllGroups();

    void update(GroupDto groupDto);

    void delete(Long id);

    GroupDto selectGroupById(Long id);

    //그룹명 중복 확인
    int existsByGroupName(String groupName);

}

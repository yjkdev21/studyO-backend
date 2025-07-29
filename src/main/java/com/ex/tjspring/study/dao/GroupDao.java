package com.ex.tjspring.study.dao;

import com.ex.tjspring.study.dto.GroupDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface GroupDao {
    void groupInsert(GroupDto groupDto);

    ArrayList<GroupDto> groupSelectAll();

    GroupDto groupSelectById(Long id);

    GroupDto groupSelectByName(String groupName);

    void groupDelete(Long id);
    void groupUpdate(GroupDto groupDto);

    //이건 맞는지 모르겠는데 중복이름 확인용 메서드
    boolean existsByGroupName(String groupName);
}
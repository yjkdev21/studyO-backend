package com.ex.tjspring.batisexample.dao;

import com.ex.tjspring.batisexample.dto.ExDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface IExDao {

    void exInsert(ExDto dto);
    List<ExDto> exSelectAllPaged(int offset, int pageSize);
    ExDto exSelectId(Long id);
    void exUpdate(ExDto dto);
    void exDelete(Long id);
    int exSelectTotalCount();
}


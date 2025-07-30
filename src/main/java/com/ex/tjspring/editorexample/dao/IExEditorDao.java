package com.ex.tjspring.editorexample.dao;

import com.ex.tjspring.editorexample.dto.ExEditorDto;
import com.ex.tjspring.editorexample.dto.ExEditorViewDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IExEditorDao {

    Long insert(ExEditorDto dto);
    List<ExEditorDto> selectAllPaged(int offset, int pageSize);
    ExEditorViewDto selectId(Long id);
    void update(ExEditorDto dto);
    void delete(Long id);
    int selectTotalCount();

}


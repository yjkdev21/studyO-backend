package com.ex.tjspring.editorexample.service;

import com.ex.tjspring.editorexample.dao.IExEditorDao;
import com.ex.tjspring.editorexample.dto.ExEditorDto;
import com.ex.tjspring.editorexample.dto.ExEditorViewDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExEditorService {
    @Autowired
    private IExEditorDao dao;

    public Long insert(ExEditorDto dto){
        Long daa = dao.insert(dto);
        //log.info(" dao={}", dto.getId());
        return dto.getId();
    }

    public List<ExEditorDto> selectAllPaged(int offset, int pageSize) {
        return dao.selectAllPaged(offset, pageSize);
    }

    public ExEditorViewDto selectId(Long id){
        return dao.selectId(id);
    }

    public void update(ExEditorDto dto){
        dao.update(dto);
    }

    public void delete(Long id) {
        dao.delete(id);
    }

    public int selectTotalCount() {
        return dao.selectTotalCount();
    }
}

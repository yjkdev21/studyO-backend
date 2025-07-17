package com.ex.tjspring.batisexample.service;

import com.ex.tjspring.batisexample.dao.IExDao;
import com.ex.tjspring.batisexample.dto.ExDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExService {
    @Autowired
    private IExDao dao;

    public void exInsert(ExDto dto){
        dao.exInsert(dto);
    }

    public List<ExDto> exSelectAllPaged(int offset, int pageSize) {
        return dao.exSelectAllPaged(offset, pageSize);
    }

    public ExDto exSelectId(Long name){
        return dao.exSelectId(name);
    }

    public void exUpdate(ExDto dto){
        dao.exUpdate(dto);
    }

    public void exDelete(Long id) {
        dao.exDelete(id);
    }

    public int exSelectTotalCount() {
        return dao.exSelectTotalCount();
    }
}

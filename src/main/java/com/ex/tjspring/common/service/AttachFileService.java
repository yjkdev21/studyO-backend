package com.ex.tjspring.common.service;

import com.ex.tjspring.common.dto.AttachFileDto;
import com.ex.tjspring.common.dao.IAttachFileDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachFileService {

    @Autowired
    private IAttachFileDao dao;

    public void insert(AttachFileDto dto){
        dao.insert(dto);
    }
    public AttachFileDto selectId(Long id){
        return dao.selectId(id);
    }
    public void delete(Long id) {
        dao.delete(id);
    }

    public AttachFileDto selectByStoredFileName(String fileName){
        return dao.selectByStoredFileName(fileName);
    };

    public void deleteByStoredFileName(String storedFileName){
        dao.deleteByStoredFileName(storedFileName);
    }
}


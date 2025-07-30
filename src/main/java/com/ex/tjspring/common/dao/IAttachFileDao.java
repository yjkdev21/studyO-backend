package com.ex.tjspring.common.dao;

import com.ex.tjspring.common.dto.AttachFileDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IAttachFileDao {
    void insert(AttachFileDto dto);
    AttachFileDto selectId(Long id);
    void delete(Long id);

    AttachFileDto selectByStoredFileName(String fileName);
    void deleteByStoredFileName(String storedFileName);


}

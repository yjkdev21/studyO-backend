package com.ex.tjspring.study.service;

import com.ex.tjspring.study.dao.SidebarDao;
import com.ex.tjspring.study.dto.SidebarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SidebarServiceImpl {
    @Autowired
    private SidebarDao sidebarDao;

    public SidebarDto getStudyInfo(Long groupId) {
        return sidebarDao.getStudyInfo(groupId);
    }

}

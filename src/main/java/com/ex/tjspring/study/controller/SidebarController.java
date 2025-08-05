package com.ex.tjspring.study.controller;

import com.ex.tjspring.study.dto.SidebarDto;
import com.ex.tjspring.study.service.SidebarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sidebar")
public class SidebarController {
    @Autowired
    private SidebarServiceImpl sidebarService;

    @GetMapping("/study/{groupId}")
    public SidebarDto getStudyInfo(@PathVariable Long groupId) {
        return sidebarService.getStudyInfo(groupId);
    }
}

package com.ex.tjspring.study.controller;

import com.ex.tjspring.study.dto.SidebarDto;
import com.ex.tjspring.study.service.SidebarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getStudyInfo(@PathVariable Long groupId) {
        SidebarDto dto = sidebarService.getStudyInfo(groupId);

        if (dto == null) {
            return ResponseEntity
                    .status(404)
                    .body("해당 스터디(" + groupId + ")는 존재하지 않습니다.");
        }

        return ResponseEntity.ok(dto);
    }
}

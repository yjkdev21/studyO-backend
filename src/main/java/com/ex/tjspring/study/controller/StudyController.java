package com.ex.tjspring.study.controller;

import com.ex.tjspring.study.dto.GroupDto;
import com.ex.tjspring.study.dto.StudyMembershipDto;
import com.ex.tjspring.study.service.GroupService;
import com.ex.tjspring.study.service.StudyMembershipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study-dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@Slf4j
public class StudyController {
    @Autowired
    private GroupService groupService;

    @Autowired
    private StudyMembershipService studyMembershipService;

    // 스터디 멤버가 아니면 접근 차단
    @GetMapping("/{groupId}/dashboard-info")
    public ResponseEntity<Map<String, Object>> getDashboardInfo(
            @PathVariable Long groupId,
            @RequestHeader("X-USER-ID") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            StudyMembershipDto membership = studyMembershipService.getMembershipByUserAndGroup(userId, groupId);
            boolean isMember = (membership != null && "ACTIVE".equals(membership.getMembershipStatus()));

            response.put("success", isMember);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("멤버십 확인 중 오류 발생: groupId={}, userId={}", groupId, userId, e);
            response.put("success", false);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{groupId}/membership/{userId}")
    public ResponseEntity<Map<String, Object>> checkMembership(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            StudyMembershipDto membership = studyMembershipService.getMembershipByUserAndGroup(userId, groupId);

            boolean isMember = membership != null && "ACTIVE".equals(membership.getMembershipStatus());

            response.put("success", true);
            response.put("isMember", isMember);

            if (isMember) {
                response.put("memberRole", membership.getMemberRole());
                response.put("nickname", membership.getNickname());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.put("success", false);
            response.put("message", "멤버십 확인 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}



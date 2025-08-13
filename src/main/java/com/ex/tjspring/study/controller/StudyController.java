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

    @GetMapping("/{groupId}/dashboard-info")
    public ResponseEntity<Map<String, Object>> getDashboardInfo(
            @PathVariable Long groupId,
            @RequestHeader("X-USER-ID") Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 권한체크
            StudyMembershipDto myMembership = studyMembershipService.getMembershipByUserAndGroup(userId, groupId);
            if (myMembership == null || !"ACTIVE".equals(myMembership.getMembershipStatus())) {
                response.put("success", false);
                response.put("message", "해당 스터디에 접근 권한이 없습니다.");
                return ResponseEntity.status(403).body(response);
            }

            // 데이터 조회
            GroupDto studyInfo = groupService.selectGroupById(groupId);
            List<StudyMembershipDto> members = studyMembershipService.getGroupMembers(groupId);

            // 현재 멤버 수 계산 (활성 멤버만)
            long currentMembers = members.stream()
                    .filter(member -> "ACTIVE".equals(member.getMembershipStatus()))
                    .count();

            // 응당 데이터 구성
            response.put("success", true);
            response.put("studyInfo", studyInfo);
            response.put("membershipInfo", myMembership);
            response.put("members", members);
            response.put("currentMembers", currentMembers);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
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



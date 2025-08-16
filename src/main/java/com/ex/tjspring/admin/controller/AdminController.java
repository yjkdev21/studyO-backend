// src/main/java/com/ex/tjspring/admin/controller/AdminController.java
package com.ex.tjspring.admin.controller;

import com.ex.tjspring.admin.model.UserModel;
import com.ex.tjspring.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ex.tjspring.admin.model.StudyGroupModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 검색 기능이 필요한 경우
    @GetMapping("/users/search")
    public ResponseEntity<List<UserModel>> searchUsers(@RequestParam("searchKeyword") String searchKeyword) {
        List<UserModel> users = adminService.searchUsers(searchKeyword);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/users/details/{userId}")
    public ResponseEntity<UserModel> getUserDetails(@PathVariable("userId") String userId) {
        UserModel user = adminService.getUserDetails(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{userId}/studies")
    public ResponseEntity<List<StudyGroupModel>> getUserStudyGroups(@PathVariable("userId") String userId) {
        List<StudyGroupModel> studies = adminService.getUserStudyGroups(userId);
        return ResponseEntity.ok(studies);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


    // 스터디 그룹 목록 조회
    @GetMapping("/groups")
    public ResponseEntity<List<StudyGroupModel>> getAllStudyGroups() {
        List<StudyGroupModel> groups = adminService.getAllStudyGroups();
        return ResponseEntity.ok(groups);
    }

    // 스터디 그룹 검색
    @GetMapping("/groups/search")
    public ResponseEntity<List<StudyGroupModel>> searchStudyGroups(@RequestParam("searchKeyword") String searchKeyword) {
        List<StudyGroupModel> groups = adminService.searchStudyGroups(searchKeyword);
        return ResponseEntity.ok(groups);
    }

    // 스터디 그룹 삭제
    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<Void> deleteStudyGroup(@PathVariable("groupId") Long groupId) {
        adminService.deleteStudyGroup(groupId);
        return ResponseEntity.noContent().build();
    }


}
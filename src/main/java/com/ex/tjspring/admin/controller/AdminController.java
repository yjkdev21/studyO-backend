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
}
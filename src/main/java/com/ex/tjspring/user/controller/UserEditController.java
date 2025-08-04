package com.ex.tjspring.user.controller;

import com.ex.tjspring.user.dto.UserUpdateRequest;
import com.ex.tjspring.user.service.UserEditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserEditController {

    private final UserEditService userEditService;

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody UserUpdateRequest dto) {
        boolean result = userEditService.updateUserInfo(dto);
        Map<String, String> response = new HashMap<>();

        if (result) {
            response.put("message", "User updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
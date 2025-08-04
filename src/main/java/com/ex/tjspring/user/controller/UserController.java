package com.ex.tjspring.user.controller;

import com.ex.tjspring.user.dto.UserRegisterRequest;
import com.ex.tjspring.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody UserRegisterRequest request) {
        return userService.registerUser(request);
    }

    //  ID 중복 확인
    @GetMapping("/check-id")
    public String checkUserId(@RequestParam String userId) {
        return userService.isUserIdExists(userId) ? "exists" : "available";
    }

    //  닉네임 중복 확인
    @GetMapping("/check-nickname")
    public String checkNickname(@RequestParam String nickname) {
        return userService.isNicknameExists(nickname) ? "exists" : "available";
    }
}

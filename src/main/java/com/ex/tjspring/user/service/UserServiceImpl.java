package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserRegisterRequest;
import com.ex.tjspring.user.mapper.UserMapper;
import com.ex.tjspring.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public User findByUserId(String userId) {
        return userMapper.findByUserId(userId);
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public User findByNickname(String nickname) {
        return userMapper.findByNickname(nickname);
    }



    @Override
    public boolean isUserIdExists(String userId) {
        return userMapper.findByUserId(userId) != null;
    }

    @Override
    public boolean isNicknameExists(String nickname) {
        return userMapper.findByNickname(nickname) != null;
    }

    @Override
    public String registerUser(UserRegisterRequest request) {
        if (isUserIdExists(request.getUserId())) {
            return "userIdExists";
        }
        if (userMapper.findByEmail(request.getEmail()) != null) {
            return "emailExists";
        }
        if (isNicknameExists(request.getNickname())) {
            return "nicknameExists";
        }

        User user = new User();
        user.setUserId(request.getUserId());
        // 평문 비밀번호 저장 (암호화 제거)
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());

        Long id = userMapper.insertUser(user);

        if (id != null && id > 0) {
            return "success";
        } else {
            return "fail";
        }
    }
}

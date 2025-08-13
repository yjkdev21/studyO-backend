// src/main/java/com/ex/tjspring/admin/service/AdminServiceImpl.java
package com.ex.tjspring.admin.service;

import com.ex.tjspring.admin.mapper.AdminMapper;
import com.ex.tjspring.admin.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;

    @Override
    public List<UserModel> getAllUsers() {
        return adminMapper.findAllUsers();
    }

    @Override
    public List<UserModel> searchUsers(String searchKeyword) {
        return adminMapper.searchUsers(searchKeyword);
    }
}
// src/main/java/com/ex/tjspring/admin/service/AdminService.java
package com.ex.tjspring.admin.service;

import com.ex.tjspring.admin.model.UserModel;

import java.util.List;

public interface AdminService {
    List<UserModel> getAllUsers();
    List<UserModel> searchUsers(String searchKeyword);
}
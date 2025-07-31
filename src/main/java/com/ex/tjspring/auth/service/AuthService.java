package com.ex.tjspring.auth.service;

import com.ex.tjspring.user.model.User;

public interface AuthService {
    User login(String userId, String password);
}

package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserRegisterRequest;
import com.ex.tjspring.user.mapper.UserMapper;
import com.ex.tjspring.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserMapper userMapper;
	// 비밀번호 BCrypt 암호화를 위한 인코더 주입
	private final PasswordEncoder passwordEncoder;

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
	public boolean isEmailExists(String email) {
		return userMapper.findByEmail(email) != null;
	}

	@Override
	public boolean isNicknameExists(String nickname) {
		return userMapper.findByNickname(nickname) != null;
	}

	@Override
	public String registerUser(UserRegisterRequest request) {
		// 중복 검사
		if (isUserIdExists(request.getUserId())) {
			return "userIdExists";
		}
		if (isEmailExists(request.getEmail())) {
			return "emailExists";
		}
		if (isNicknameExists(request.getNickname())) {
			return "nicknameExists";
		}

		User user = new User();
		user.setUserId(request.getUserId());

		// BCrypt로 비밀번호 암호화 - 평문 비밀번호를 BCrypt 해시로 변환(매번 다른 해시값 생성)
		String hashPassword = passwordEncoder.encode(request.getPassword());
		user.setPassword(hashPassword);
//        user.setPassword(request.getPassword());
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

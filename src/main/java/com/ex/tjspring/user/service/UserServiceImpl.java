package com.ex.tjspring.user.service;

import com.ex.tjspring.user.dto.UserRegisterRequest;
import com.ex.tjspring.user.mapper.UserMapper;
import com.ex.tjspring.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
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

	// 아이디 찾기
	@Override
	public String findUserIdByEmail(String email) {
		User user = userMapper.findByEmail(email);
		if (user != null && "N".equals(user.getIsDeleted())) {
			return user.getUserId();
		}
		return null;
	}

	// 비밀번호 찾기
	@Override
	public boolean resetPassword(String userId, String email, String newPassword) {
		User user = userMapper.findByUserId(userId);

		// 사용자가 존재하고 이메일이 일치, 탈퇴하지 않은 경우
		if(user != null && email.equals(user.getEmail()) && "N".equals(user.getIsDeleted())) {
			String hashedPassword = passwordEncoder.encode(newPassword);
			user.setPassword(hashedPassword);
			userMapper.updateUser(user);

			log.info("비밀번호 변경 완료: userId={}", userId);

			return true;
		}
		return false;
	}

	// 회원 탈퇴
	@Override
	public boolean deleteUser(String userId) {
		User user = userMapper.findByUserId(userId);

		//	회원이 존재하지 않거나 이미 탈퇴한 경우
		if (user == null || "Y".equals(user.getIsDeleted())){
			return false;
		}

		userMapper.updateIsDeleted(user.getId());
		return true;
	}

    @Override
    public boolean verifyPassword(String userId, String rawPassword) {
        User user = userMapper.findByUserId(userId);

        if (user == null || "Y".equals(user.getIsDeleted())) {
            return false;
        }

        // BCrypt로 암호화된 비밀번호와 평문 비밀번호 비교
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}

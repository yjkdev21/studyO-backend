package com.ex.tjspring.study.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyMembershipDto {
	private Long id;

	@NotNull(message = "사용자 ID는 필수입니다.")
	private Long userId;

	@NotNull(message = "그룹 ID는 필수입니다.")
	private Long groupId;

	private Date joinedAt;
	private Date leftAt;

	@Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "멤버십 상태는 ACTIVE 또는 INACTIVE만 가능합니다")
	private String membershipStatus;

	@Pattern(regexp = "^(ADMIN|MEMBER)$", message = "멤버 역할은 ADMIN 또는 MEMBER만 가능합니다")
	private String memberRole;

	@Size(max = 20)
	private String nickname;
	private String profileImage;
}

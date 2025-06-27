package com.example.luvisluvproject.domain.member.enums;

import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

public enum UserRole {
	// USER(일반 회원), MANAGER(가게 사장님), ADMIN(관리자);
	USER, MANAGER, ADMIN;

	// userrole 타입변경 메서드 string -> userrole
	public static UserRole of(String role) {
		for (UserRole userRole : UserRole.values()) {
			if (userRole.name().equalsIgnoreCase(role)) {
				return userRole;
			}
		}
		throw new CustomRuntimeException(ExceptionCode.USER_ROLE_NOT_FOUND);
	}

}

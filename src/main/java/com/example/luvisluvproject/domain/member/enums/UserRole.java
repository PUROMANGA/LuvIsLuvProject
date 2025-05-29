package com.example.luvisluvproject.domain.member.enums;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

public enum UserRole {
	USER, MANAGER;

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

package com.example.luvisluvproject.domain.notify;

import lombok.Getter;

@Getter
public enum NotifyCategory {
	Message("메세지가 도착했습니다."),
	Match("매칭이 도착했습니다."),
	SUCCESS_MATCH("매칭이 성사되었습니다."),
	LOG("로그가 도착했습니다");

	private final String content;

	NotifyCategory(String content) {
		this.content = content;
	}
}

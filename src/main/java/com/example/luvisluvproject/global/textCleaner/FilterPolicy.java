package com.example.luvisluvproject.global.textCleaner;

import java.util.Set;

import lombok.Getter;

@Getter
public enum FilterPolicy {

	NUMBERS("[\\p{N}]"),
	WHITESPACES("[\\s]"),
	FOREIGNLANGUAGES("[\\p{L}&&[^ㄱ-ㅎ가-힣ㅏ-ㅣa-zA-Z]]");

	private final String regex;

	FilterPolicy(String regex) {
		this.regex = regex;
	}

	public boolean checkName(Set<String> requestTagNames) {
		for (String name : requestTagNames) {
			if (name.matches((".*" + this.regex + ".*"))) {
				return false;
			}
		}
		return true;
	}

	public String apply(String name) {
		return name.replace(regex, "");
	}
}

package com.example.luvisluvproject.global.textCleaner;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class TextValidatorHandler {

	public Set<String> filterPolicyHandler(Set<String> requestTagNames) {
		if (FilterPolicy.NUMBERS.checkName(requestTagNames)) {
			requestTagNames = requestTagNames.stream()
				.map(FilterPolicy.NUMBERS::apply)
				.collect(Collectors.toSet());
			System.out.println("🔢 숫자 정제 적용됨: " + requestTagNames);
		}

		if (FilterPolicy.WHITESPACES.checkName(requestTagNames)) {
			requestTagNames = requestTagNames.stream()
				.map(FilterPolicy.WHITESPACES::apply)
				.collect(Collectors.toSet());
			System.out.println("␣ 공백 정제 적용됨: " + requestTagNames);
		}

		if (FilterPolicy.FOREIGNLANGUAGES.checkName(requestTagNames)) {
			requestTagNames = requestTagNames.stream()
				.map(FilterPolicy.FOREIGNLANGUAGES::apply)
				.collect(Collectors.toSet());
			System.out.println("🌍 외국어 정제 적용됨: " + requestTagNames);
		}

		return requestTagNames;
	}
}

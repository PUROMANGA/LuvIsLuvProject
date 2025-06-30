package com.example.luvisluvproject.global.textCleaner;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class TextValidatorHandler {

	public Set<String> filterPolicyHandler(Set<String> requestTagNames) {
		if (FilterPolicy.NUMBERS.checkName(requestTagNames)) {
			requestTagNames = requestTagNames.stream().map(FilterPolicy.NUMBERS::apply).collect(Collectors.toSet());
		}

		if (FilterPolicy.WHITESPACES.checkName(requestTagNames)) {
			requestTagNames = requestTagNames.stream().map(FilterPolicy.WHITESPACES::apply).collect(Collectors.toSet());
		}

		if (FilterPolicy.FOREIGNLANGUAGES.checkName(requestTagNames)) {
			requestTagNames = requestTagNames.stream().map(FilterPolicy.FOREIGNLANGUAGES::apply).collect(Collectors.toSet());
		}

		return requestTagNames;
	}
}

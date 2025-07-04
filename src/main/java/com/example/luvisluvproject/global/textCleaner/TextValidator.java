package com.example.luvisluvproject.global.textCleaner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

@Component
public class TextValidator {

	private final TextValidatorHandler textValidatorHandler;
	private final RedisTemplate<String, String> redisTemplate;
	private final Komoran komoran;

	//토큰템플릿이라 적혀있지만 스트링-스트링 구조의 레디스 템플릿임
	public TextValidator(TextValidatorHandler textValidatorHandler, @Qualifier("customStringRedisTemplate")RedisTemplate<String, String> redisTemplate,
		Komoran komoran) {
		this.textValidatorHandler = textValidatorHandler;
		this.redisTemplate = redisTemplate;
		this.komoran = komoran;
	}

	public Set<String> analyzerName(Set<String> requestTagNames) {
		Set<String> afterPolicyHandlerName = textValidatorHandler.filterPolicyHandler(requestTagNames);

		for (String tagNames : afterPolicyHandlerName) {
			KomoranResult analyzeResultList = komoran.analyze(tagNames);
			tokens.addAll(analyzeResultList.getTokenList());
		}

		System.out.println("====================================================");
		String combined = tokens.stream()
			.map(Token::getMorph)
			.collect(Collectors.joining());
		System.out.println("🧩 전체 조합 문자열: " + combined);
		System.out.println("====================================================");

		Set<String> filterNames = new HashSet<>();

		for (int i = 0; i < combined.length(); i++) {
			for (int j = i + 1; j <= combined.length(); j++) {
				filterNames.add(combined.substring(i, j));
			}
		}

		for (Token token : tokens) {
			String pos = token.getPos();
			String word = token.getMorph();
			if (pos.startsWith("NN") || pos.startsWith("VA") || pos.startsWith("VV") || pos.equals("ETM")) {
				filterNames.add(word);
			}
		}


		filterNames.addAll(afterPolicyHandlerName);
		System.out.println("✅ [최종 금칙어 검사 대상]");
		System.out.println(filterNames);
		System.out.println("====================================================");

		return filterNames;
	}

	public boolean analyzerText(Set<String> filterNames) {
		System.out.println("====================================================");
		System.out.println("🚫 [금칙어 검사 시작]");
		Set<String> banwords = redisTemplate.opsForSet().members("banwords");
		System.out.println("금칙어 수: " + banwords.size());

		for (String tagName : filterNames) {
			for (String ban : banwords) {
				if (tagName.contains(ban)) {
					System.out.println("❌ 금칙어 발견! tag=\"" + tagName + "\", 금칙어=\"" + ban + "\"");
					System.out.println("====================================================");
					return false;
				}
			}
		}
		System.out.println("✅ 금칙어 없음. 통과!");
		System.out.println("====================================================");
		return true;
	}
}

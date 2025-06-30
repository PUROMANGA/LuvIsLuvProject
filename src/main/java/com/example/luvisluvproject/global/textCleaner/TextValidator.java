package com.example.luvisluvproject.global.textCleaner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;

@Component
public class TextValidator {

	private final TextValidatorHandler textValidatorHandler;
	private final RedisTemplate<String, String> redisTemplate;
	private final Komoran komoran;

	//토큰템플릿이라 적혀있지만 스트링-스트링 구조의 레디스 템플릿임
	public TextValidator(TextValidatorHandler textValidatorHandler, @Qualifier("tokenRedisTemplate")RedisTemplate<String, String> redisTemplate,
		Komoran komoran) {
		this.textValidatorHandler = textValidatorHandler;
		this.redisTemplate = redisTemplate;
		this.komoran = komoran;
	}

	Set<String> filterNames = new HashSet<>();

	public Set<String> analyzerName(Set<String> requestTagNames) {
		Set<String> afterPolicyHandlerName = textValidatorHandler.filterPolicyHandler(requestTagNames);

		for(String tagNames : afterPolicyHandlerName) {
			KomoranResult analyzeResultList = komoran.analyze(tagNames);
			List<String> nouns = analyzeResultList.getNouns();
			filterNames.addAll(nouns);
		}

		return filterNames;
	}

	public boolean analyzerText(Set<String> filterNames) {
		for(String tagName : filterNames) {
			if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("banwords", tagName))) {
				return false;
			}
		}
		return true;
	}
}

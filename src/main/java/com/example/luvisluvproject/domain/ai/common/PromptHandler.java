package com.example.luvisluvproject.domain.ai.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.memberInteractionLog.dto.MemberInteractionLogDto;

@Component
public class PromptHandler {

	public Prompt createPrompt(MemberInteractionLogDto todayMemberInteractionLogDto,
		MemberInteractionLogDto yesterDayMemberInteractionLogDto) {
		PromptTemplate template = getPromptTemplate();

		Map<String, Object> vars = new LinkedHashMap<>();
		vars.put("yesterdayGetMatchCount", yesterDayMemberInteractionLogDto.getGetMatchCount());
		vars.put("yesterdayMatchReceivedCount", yesterDayMemberInteractionLogDto.getMatchReceivedCount());
		vars.put("yesterdayMatchRequestCount", yesterDayMemberInteractionLogDto.getMatchRequestCount());
		vars.put("yesterdayMessageCount", yesterDayMemberInteractionLogDto.getMessageCount());
		vars.put("yesterdayMatchScore", yesterDayMemberInteractionLogDto.getMatchScore());
		vars.put("yesterdayTags", yesterDayMemberInteractionLogDto.getTags());

		vars.put("todayGetMatchCount", todayMemberInteractionLogDto.getGetMatchCount());
		vars.put("todayMatchReceivedCount", todayMemberInteractionLogDto.getMatchReceivedCount());
		vars.put("todayMatchRequestCount", todayMemberInteractionLogDto.getMatchRequestCount());
		vars.put("todayMessageCount", todayMemberInteractionLogDto.getMessageCount());
		vars.put("todayMatchScore", todayMemberInteractionLogDto.getMatchScore());
		vars.put("todayTags", todayMemberInteractionLogDto.getTags());

		return template.create(vars);
	}

	public PromptTemplate getPromptTemplate() {
		String templateText = """
			회원 분석 요청입니다.
			
			[ 어제 기록 ]
			
			- 매칭 시도: {yesterdayGetMatchCount}회
			- 수락한 매칭: {yesterdayMatchReceivedCount}회
			- 신청한 횟수: {yesterdayMatchRequestCount}회
			- 메세지 보낸 횟수: {yesterdayMessageCount}회
			- 매칭 점수: {yesterdayMatchScore}
			- 선호 태그: {yesterdayTags}
			
			[ 오늘 기록 ]
			
			- 매칭 시도: {todayGetMatchCount}회
			- 수락한 매칭: {todayMatchReceivedCount}회
			- 신청한 횟수: {todayMatchRequestCount}회
			- 메세지 보낸 횟수: {todayMessageCount}회
			- 매칭 점수: {todayMatchScore}
			- 선호 태그: {todayTags}
			
			이 사용자의 어제 기록과 오늘 기록을 비교해서 활동 경향과 특징을 요약해줘.
			""";

		return new PromptTemplate(templateText);
	}
}

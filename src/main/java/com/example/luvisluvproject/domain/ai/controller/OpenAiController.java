package com.example.luvisluvproject.domain.ai.controller;

import java.time.LocalDate;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.ai.common.PromptHandler;
import com.example.luvisluvproject.domain.ai.service.OpenAiService;
import com.example.luvisluvproject.domain.memberInteractionLog.dto.MemberInteractionLogDto;
import com.example.luvisluvproject.global.common.AuthUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class OpenAiController {

	private final OpenAiChatModel openAiChatModel;
	private final OpenAiService openAiService;
	private final PromptHandler promptHandler;

	public OpenAiController(OpenAiChatModel openAiChatModel, OpenAiService openAiService, PromptHandler promptHandler) {
		this.openAiChatModel = openAiChatModel;
		this.openAiService = openAiService;
		this.promptHandler = promptHandler;
	}

	@GetMapping("/ai/ask")
	public String askAI(@AuthenticationPrincipal AuthUser authUser) {

		LocalDate now = LocalDate.now();
		LocalDate yesterDay = LocalDate.now().minusDays(1);

		MemberInteractionLogDto todayMemberInteractionLogDto = openAiService.getMemberInteractionLogDto(
			authUser.getUsername(), now);
		MemberInteractionLogDto yesterDayMemberInteractionLogDto = openAiService.getMemberInteractionLogDto(
			authUser.getUsername(), yesterDay);

		Prompt prompt = promptHandler.createPrompt(todayMemberInteractionLogDto, yesterDayMemberInteractionLogDto);
		return openAiChatModel.call(prompt).getResult().getOutput().getText();
	}
}

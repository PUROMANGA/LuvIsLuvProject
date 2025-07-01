package com.example.luvisluvproject.global.batch.memberInteractionLog;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberInteractionLog;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MemberInteractionLogConfig {

	private final MemberInteractionLogWriter memberInteractionLogWriter;
	private final MemberInteractionLogReader memberInteractionLogReader;

	@Bean(name = "memberInteractionLogSaveJob")
	public Job memberInteractionLogSaveJob(JobRepository jobRepository, Step memberInteractionLogSaveJobStep) {
		return new JobBuilder("memberInteractionLogSaveJob", jobRepository)
			.start(memberInteractionLogSaveJobStep)
			.build();
	}

	@Bean(name = "memberInteractionLogSaveJobStep")
	public Step memberInteractionLogSaveJobStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("memberInteractionLogSaveJobStep", jobRepository)
			.<MemberInteractionLog, MemberInteractionLog>chunk(100, transactionManager)
			.reader(memberInteractionLogReader)
			.writer(memberInteractionLogWriter)
			.build();
	}
}

package com.example.luvisluvproject.global.batch.memberTagLikeBatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.luvisluvproject.domain.memberInteractionLog.entity.MemberTagLikeCount;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MemberTagLikeCountConfig {

	private final MemberTagLikeCountWriter memberTagLikeCountWriter;
	private final MemberTagLikeCountReader memberTagLikeCountReader;

	@Bean(name = "memberTagLikeCountSaveJob")
	public Job memberTagLikeCountSaveJob(JobRepository jobRepository, Step memberInteractionLogSaveJobStep) {
		return new JobBuilder("memberTagLikeCountSaveJob", jobRepository)
			.start(memberInteractionLogSaveJobStep)
			.build();
	}

	@Bean(name = "memberTagLikeCountSaveJobStep")
	public Step memberTagLikeCountSaveJobStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("memberTagLikeCountSaveJobStep", jobRepository)
			.<MemberTagLikeCount, MemberTagLikeCount>chunk(100, transactionManager)
			.reader(memberTagLikeCountReader)
			.writer(memberTagLikeCountWriter)
			.build();
	}
}
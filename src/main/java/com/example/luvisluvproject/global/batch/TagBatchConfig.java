package com.example.luvisluvproject.global.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.luvisluvproject.domain.tag.entity.Tag;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TagBatchConfig {

	private final TagRedisReader tagRedisReader;
	private final TagWriter tagWriter;

	@Bean
	public Job footballJob(JobRepository jobRepository, Step step) {
		return new JobBuilder("tagSaveJob", jobRepository)
			.start(step)
			.build();
	}

	@Bean
	public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		return new StepBuilder("TagSaveJob", jobRepository)
			.<Tag, Tag>chunk(100, transactionManager)
			.reader(tagRedisReader)
			.writer(tagWriter)
			.build();
	}
}

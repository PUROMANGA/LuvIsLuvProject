package com.example.luvisluvproject.global.batch.memberTagLikeBatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.global.batch.batchcommon.JobParmatersHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MemberTagLikeCountScheduler {

	private final JobLauncher jobLauncher;
	private final Job memberTagLikeCountSaveJob;
	private final JobParmatersHandler jobParmatersHandler;

	public MemberTagLikeCountScheduler(JobLauncher jobLauncher, @Qualifier("memberTagLikeCountSaveJob")Job memberTagLikeCountSaveJob,
		JobParmatersHandler jobParmatersHandler) {
		this.jobLauncher = jobLauncher;
		this.memberTagLikeCountSaveJob = memberTagLikeCountSaveJob;
		this.jobParmatersHandler = jobParmatersHandler;
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void runJob() {
		try {
			jobLauncher.run(memberTagLikeCountSaveJob, jobParmatersHandler.jobParameters());

		} catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
				 | JobParametersInvalidException | JobRestartException e) {
			log.error(e.getMessage());
		}
	}
}

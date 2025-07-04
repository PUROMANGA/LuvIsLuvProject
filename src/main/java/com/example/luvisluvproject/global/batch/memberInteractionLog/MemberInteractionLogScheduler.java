package com.example.luvisluvproject.global.batch.memberInteractionLog;

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
public class MemberInteractionLogScheduler {

	private final JobLauncher jobLauncher;
	private final Job memberInteractionLogSaveJob;
	private final JobParmatersHandler jobParmatersHandler;

	public MemberInteractionLogScheduler(JobLauncher jobLauncher, @Qualifier("memberInteractionLogSaveJob")Job memberInteractionLogSaveJob,
		JobParmatersHandler jobParmatersHandler) {
		this.jobLauncher = jobLauncher;
		this.memberInteractionLogSaveJob = memberInteractionLogSaveJob;
		this.jobParmatersHandler = jobParmatersHandler;
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void runJob() {
		try {
			jobLauncher.run(memberInteractionLogSaveJob, jobParmatersHandler.jobParameters());

		} catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
				 | JobParametersInvalidException | JobRestartException e) {
			log.error(e.getMessage());
		}
	}
}

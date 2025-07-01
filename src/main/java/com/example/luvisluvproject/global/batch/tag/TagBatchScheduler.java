package com.example.luvisluvproject.global.batch.tag;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.global.batch.batchcommon.JobParmatersHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TagBatchScheduler {

	private final JobLauncher jobLauncher;
	private final Job tagSaveJob;
	private final JobParmatersHandler jobParmatersHandler;

	public TagBatchScheduler(JobLauncher jobLauncher, @Qualifier("tagSaveJob")Job tagSaveJob, JobParmatersHandler jobParmatersHandler) {
		this.jobLauncher = jobLauncher;
		this.tagSaveJob = tagSaveJob;
		this.jobParmatersHandler = jobParmatersHandler;
	}

	@Scheduled(cron = "0 0 0 * * ?")
	public void runJob() {
		try {
			jobLauncher.run(tagSaveJob, jobParmatersHandler.jobParameters());

		} catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
				 | JobParametersInvalidException | JobRestartException e) {
			log.error(e.getMessage());
		}
	}
}

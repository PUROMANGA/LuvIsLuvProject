package com.example.luvisluvproject.global.batch;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TagBatchScheduler {

	private final JobLauncher jobLauncher;
	private final Job footballJob;

	@Scheduled(cron = "0 0 0 * * ?")
	public void runJob() {
		Map<String, JobParameter<?>> parameterMap = new HashMap<>();
		parameterMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
		JobParameters jobParameters = new JobParameters(parameterMap);
		// JobParameters jobParameters = new JobParametersBuilder()
		// 	.addLong("time", System.currentTimeMillis())
		// 	.toJobParameters();
		try {
			jobLauncher.run(footballJob, jobParameters);

		} catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
				 | JobParametersInvalidException | JobRestartException e) {
			log.error(e.getMessage());
		}
	}
}

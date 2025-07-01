package com.example.luvisluvproject.global.batch.batchcommon;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

@Component
public class JobParmatersHandler {

	public JobParameters jobParameters() {
		Map<String, JobParameter<?>> parameterMap = new HashMap<>();
		parameterMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
		JobParameters jobParameters = new JobParameters(parameterMap);
		return jobParameters;
	}

	// JobParameters jobParameters = new JobParametersBuilder()
	// 	.addLong("time", System.currentTimeMillis())
	// 	.toJobParameters();
}

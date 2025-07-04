package com.example.luvisluvproject.domain.batch;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.example.luvisluvproject.domain.memberInteractionLog.repository.MemberInteractionLogRepository;
import com.example.luvisluvproject.global.batch.memberInteractionLog.MemberInteractionLogConfig;
import com.example.luvisluvproject.global.batch.memberInteractionLog.MemberInteractionLogReader;
import com.example.luvisluvproject.global.batch.memberInteractionLog.MemberInteractionLogWriter;

@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest
public class MemberInteractionLogBatchTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private Job memberInteractionLogSaveJob;

	@Autowired
	private RedisTemplate<String, String> stringcustomStringRedisTemplate;

	@Autowired
	private MemberInteractionLogRepository memberInteractionLogRepository;

	@Autowired
	private MemberInteractionLogReader memberInteractionLogReader;

	@Autowired
	private MemberInteractionLogWriter memberInteractionLogWriter;

	@Autowired
	private MemberInteractionLogConfig memberInteractionLogConfig;

	@BeforeEach
	void setUp() {
		jobLauncherTestUtils.setJob(memberInteractionLogSaveJob);
	}

	@Test
	@DisplayName("유저 정보 인터페이스 전체 테스트")
	void memberInteractionLogTest() throws Exception {
		//given
		Map<String, JobParameter<?>> parameterMap = new HashMap<>();
		parameterMap.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));
		JobParameters jobParameters = new JobParameters(parameterMap);

		//when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

		//then
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
}

package com.example.luvisluvproject.domain.batch;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

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

import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;
import com.example.luvisluvproject.global.batch.TagBatchConfig;
import com.example.luvisluvproject.global.batch.TagRedisReader;
import com.example.luvisluvproject.global.batch.TagWriter;

@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest
public class batchTagTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private Job footballJob;

	@Autowired
	private RedisTemplate<String, Tag> tagRedisTemplate;

	@Autowired
	private TagJpaRepository tagJpaRepository;

	@Autowired
	private TagRedisReader tagRedisReader;

	@Autowired
	private TagWriter tagWriter;

	@Autowired
	private TagBatchConfig tagBatchConfig;

	@Test
	@DisplayName("태그 배치 전체 테스트")
	void tagBatchTest() throws Exception {

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

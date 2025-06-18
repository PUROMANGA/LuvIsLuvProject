package com.example.luvisluvproject.global.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;


@TestConfiguration
public class TestConfig {
	@Bean
	public JwtFilter jwtFilter() {
		return Mockito.mock(JwtFilter.class);
	}

	@Bean
	public JpaMetamodelMappingContext jpaMappingContext() {
		return Mockito.mock(JpaMetamodelMappingContext.class);
	}

	@Bean
	public IsNewAwareAuditingHandler mongoAuditingHandler() {
		return Mockito.mock(IsNewAwareAuditingHandler.class);
	}


	@Bean
	public MongoMappingContext mongoMappingContext() {
		return Mockito.mock(MongoMappingContext.class);
	}
}

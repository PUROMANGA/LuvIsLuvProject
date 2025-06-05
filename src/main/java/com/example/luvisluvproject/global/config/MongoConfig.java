package com.example.luvisluvproject.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor

public class MongoConfig {

	@Bean
	public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory,
		MappingMongoConverter mappingMongoConverter) {

		MongoTemplate template = new MongoTemplate(mongoDatabaseFactory, mappingMongoConverter);

		// TTL 인덱스 생성
		Index ttlIndex = new Index().on("creatTime", Sort.Direction.ASC).expire(2592000);
		template.indexOps("messages").createIndex(ttlIndex); // 또는 createIndex

		return template;
	}

	@Bean
	public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDatabaseFactory,
		MongoMappingContext mongoMappingContext) {

		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);

		converter.setTypeMapper(new DefaultMongoTypeMapper(null));

		return converter;
	}
}

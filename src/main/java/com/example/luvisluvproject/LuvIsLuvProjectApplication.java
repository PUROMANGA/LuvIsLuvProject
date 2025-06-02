package com.example.luvisluvproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableMongoAuditing
public class LuvIsLuvProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LuvIsLuvProjectApplication.class, args);
	}

}

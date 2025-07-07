package com.example.luvisluvproject.admin.sanction.common;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class SanctionHandler {

	public Duration makeDuration (LocalDateTime sanction) {
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(now, sanction);

		if (duration.isNegative() || duration.isZero()) {
			duration = Duration.ofSeconds(1);
		}

		return duration;
	}
}

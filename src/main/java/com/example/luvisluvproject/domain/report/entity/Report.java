package com.example.luvisluvproject.domain.report.entity;

import com.example.luvisluvproject.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id", nullable = false)
	private Member reporter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reported_id", nullable = false)
	private Member reported;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReportReason reason;

	@Column(columnDefinition = "TEXT")
	private String description;

	private LocalDateTime createdAt = LocalDateTime.now();
}
package com.example.luvisluvproject.global.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)

public class BaseEntity {

	@CreatedDate // 생성시 자동입력
	@Column(updatable = false)
	private LocalDateTime creatTime;

	@LastModifiedDate
	private LocalDateTime modifiedTime;
}

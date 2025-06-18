package com.example.luvisluvproject.domain.tag.dto;

import lombok.*;

import java.io.Serializable;

/**
 * Redis에 임시 저장되는 태그 정보 객체
 * 태그 이름, 카테고리, 생성자 유형, 우선순위, 연결할 유저 ID 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CachedTagDto implements Serializable {

	/**
	 * 태그 이름
	 */
	private String name;

	/**
	 * 태그 카테고리 (ex: HOBBY, GENDER_IDENTITY 등)
	 */
	private String category;

	/**
	 * 생성자 유형 (USER 또는 ADMIN)
	 */
	private String createdByType;

	/**
	 * 태그 활성 여부
	 */
	private boolean active;

	/**
	 * 노출 우선순위
	 */
	private int priority;

	/**
	 * 해당 태그를 생성한 사용자 ID
	 */
	private Long memberId;
}

package com.example.luvisluvproject.domain.tag.dto;

import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 태그 생성/수정 시 사용하는 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDto {

	/**
	 * 태그 이름 (예: 독서, 캠핑 등)
	 */
	@NotBlank(message = "태그 이름은 공백일 수 없습니다.")
	private String name;

	/**
	 * 태그 카테고리 (예: HOBBY, GENDER_IDENTITY 등)
	 */
	private String category;

	/**
	 * 생성자 유형 (USER 또는 ADMIN)
	 */
	private TagCreatedByType createdByType;

	/**
	 * 사용 가능 여부
	 */
	private boolean active = true;

	/**
	 * 노출 우선순위 (높을수록 먼저 추천됨)
	 */
	private int priority = 0;
}

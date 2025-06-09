package com.example.luvisluvproject.domain.tag.dto;

import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDto {

	/**
	 * 태그 이름 (예: "캠핑", "독서", "고양이")
	 */
	@NotBlank(message = "태그 이름은 공백일 수 없습니다.")
	private String name;

	/**
	 * 태그 카테고리 (예: "취미", "성격", "라이프스타일")
	 */
	private String category;

	/**
	 * 생성 출처: 사용자(User), 관리자(Admin)
	 */
	private TagCreatedByType createdByType;

	/**
	 * 태그 활성 상태 (false인 경우 추천, 노출 등 제외)
	 */
	private boolean active = true;

	/**
	 * 노출 우선순위 (높을수록 상단에 추천됨)
	 */
	private int priority = 0;
}

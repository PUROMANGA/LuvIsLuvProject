package com.example.luvisluvproject.domain.tag.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 유저가 태그를 선택할 때 사용하는 요청 DTO
 */
@Getter
@NoArgsConstructor
public class SelectTagsRequestDto {

	/**
	 * 선택한 태그 ID 목록 (필수)
	 */
	@NotEmpty(message = "선택할 태그 ID 리스트가 비어있을 수 없습니다.")
	private List<Long> tagIds;

	/**
	 * 선택 이유 (선택적)
	 */
	private String reason;

	/**
	 * 태그 선택 맥락 (예: 프로필 등록, 매칭 취향 설정)
	 */
	private String context;

	/**
	 * 유저가 최종 확인을 눌렀는지 여부
	 */
	private boolean confirmed;

	/**
	 * 태그 요청 시각
	 */
	private LocalDateTime requestedAt = LocalDateTime.now();  // 서버에서 기본 설정
}

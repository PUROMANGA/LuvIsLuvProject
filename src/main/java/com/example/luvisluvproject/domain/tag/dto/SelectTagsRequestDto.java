package com.example.luvisluvproject.domain.tag.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SelectTagsRequestDto {

	@NotEmpty(message = "선택할 태그 ID 리스트가 비어있을 수 없습니다.")
	private List<Long> tagIds;

	private String reason;            // 예: "저는 독서를 좋아합니다."
	private String context;           // 예: "프로필 등록 중", "매칭 취향 설정"
	private boolean confirmed;        // 예: 최종 제출 버튼 여부
	private LocalDateTime requestedAt;

	public SelectTagsRequestDto(List<Long> tagIds, String reason, String context, boolean confirmed) {
		this.tagIds = tagIds;
		this.reason = reason;
		this.context = context;
		this.confirmed = confirmed;
		this.requestedAt = LocalDateTime.now(); // 백엔드에서 자동 설정
	}
}

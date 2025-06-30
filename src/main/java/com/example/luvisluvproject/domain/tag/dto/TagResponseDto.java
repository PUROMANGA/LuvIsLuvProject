// package com.example.luvisluvproject.domain.tag.dto;
//
// import com.example.luvisluvproject.domain.tag.entity.Tag;
// import com.example.luvisluvproject.domain.tag.enums.TagCategory;
//
// import lombok.Builder;
// import lombok.Getter;
//
// /**
//  * 태그 조회 응답 DTO
//  */
// @Getter
// @Builder
// public class TagResponseDto {
// 	private Long id;
// 	private String name;
// 	private TagCategory category;
// 	private Boolean active;
// 	private Integer priority;
//
// 	/**
// 	 * Entity → DTO 변환
// 	 */
// 	public static TagResponseDto from(Tag tag) {
// 		return TagResponseDto.builder()
// 			.id(tag.getId())
// 			.name(tag.getName())
// 			.category(tag.getCategory())
// 			.active(tag.isActive())
// 			.priority(tag.getPriority())
// 			.build();
// 	}
// }

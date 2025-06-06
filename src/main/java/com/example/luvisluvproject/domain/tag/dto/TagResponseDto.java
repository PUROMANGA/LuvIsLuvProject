package com.example.luvisluvproject.domain.tag.dto;

import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagResponseDto {
	private Long id;
	private String name;
	private TagCategory category;
	private TagCreatedByType createdByType;
	private Boolean active;
	private Integer priority;

	public static TagResponseDto from(Tag tag) {
		return TagResponseDto.builder()
			.id(tag.getId())
			.name(tag.getName())
			.category(tag.getCategory())
			.createdByType(tag.getCreatedByType())
			.active(tag.isActive())
			.priority(tag.getPriority())
			.build();
	}
}

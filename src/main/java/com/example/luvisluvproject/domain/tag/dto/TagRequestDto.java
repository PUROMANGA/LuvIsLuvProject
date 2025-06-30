package com.example.luvisluvproject.domain.tag.dto;

import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagRequestDto {

	/**
	 * 태그 이름
	 */
	@NotBlank(message = "태그 이름은 공백일 수 없습니다.")
	private String name;

	/**
	 * 태그 카테고리
	 */
	@NotNull(message = "태그 카테고리는 공백일 수 없습니다.")
	private TagCategory category;
}

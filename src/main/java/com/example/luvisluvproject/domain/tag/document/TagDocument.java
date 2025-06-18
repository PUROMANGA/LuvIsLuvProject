package com.example.luvisluvproject.domain.tag.document;

import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch 인덱싱용 태그 도큐먼트
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "tags")
public class TagDocument {

	@Id
	private Long id;

	/**
	 * 태그 이름 (자동완성 검색을 위한 EdgeNGram analyzer 적용 필드)
	 */
	@Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
	private String name;

	/**
	 * 태그 카테고리 (예: HOBBY, GENDER_IDENTITY 등)
	 */
	@Field(type = FieldType.Keyword)
	private String category;

	/**
	 * 생성자 유형 (USER 또는 ADMIN)
	 */
	@Field(type = FieldType.Keyword)
	private TagCreatedByType createdByType;

	/**
	 * 현재 사용 가능한 태그인지 여부
	 */
	@Field(type = FieldType.Boolean)
	private boolean active;

	/**
	 * 노출 우선순위 (높을수록 먼저 추천됨)
	 */
	@Field(type = FieldType.Integer)
	private int priority;

	/**
	 * Elasticsearch 문서를 TagResponseDto로 변환
	 */
	public TagResponseDto toDto() {
		return TagResponseDto.builder()
			.id(this.id)
			.name(this.name)
			.category(TagCategory.from(this.category))
			.createdByType(this.createdByType)
			.active(this.active)
			.priority(this.priority)
			.build();
	}
}

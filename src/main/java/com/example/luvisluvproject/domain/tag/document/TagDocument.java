package com.example.luvisluvproject.domain.tag.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "tags")
public class TagDocument {

	@Id
	private Long id;

	/**
	 * 태그 이름 (부분 검색용 - analyzer 적용)
	 */
	@Field(type = FieldType.Text, analyzer = "autocomplete", searchAnalyzer = "standard")
	private String name;

	/**
	 * 태그 카테고리 (ex: 취미, 성격 등)
	 */
	@Field(type = FieldType.Keyword)
	private String category;

	/**
	 * 생성자 유형 (User, Admin)
	 */
	@Field(type = FieldType.Keyword)
	private TagCreatedByType createdByType;

	/**
	 * 사용 가능한 태그인지 여부
	 */
	@Field(type = FieldType.Boolean)
	private boolean active;

	/**
	 * 노출 우선순위 (높을수록 먼저 보임)
	 */
	@Field(type = FieldType.Integer)
	private int priority;
}

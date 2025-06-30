package com.example.luvisluvproject.domain.tag.entity;

import java.util.Objects;

import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tags")
public class Tag extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 태그 이름
	 */
	@Column(nullable = false, unique = true, length = 50)
	private String name;

	/**
	 * 태그 카테고리
	 */
	@Column(length = 30)
	private TagCategory category;

	public Tag(String name, TagCategory category) {
		this.name = name;
		this.category = category;
	}

	public Tag(TagRequestDto tagRequestDto) {
		this.name = tagRequestDto.getName();
		this.category = tagRequestDto.getCategory();
	}

	public Tag(MemberTag memberTag) {
		this.name = memberTag.getTagName();
		this.category = memberTag.getCategory();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Tag))
			return false;
		Tag tag = (Tag)o;
		return Objects.equals(this.name, tag.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}

package com.example.luvisluvproject.domain.tag.entity;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
	name = "member_tags",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"member_id", "tag_name"})
	}
)
public class MemberTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	private String tagName;

	@Enumerated(EnumType.STRING)
	private TagCategory category;

	public MemberTag(Long memberId, String tagName, TagCategory category) {
		this.memberId = memberId;
		this.tagName = tagName;
		this.category = category;
	}
}

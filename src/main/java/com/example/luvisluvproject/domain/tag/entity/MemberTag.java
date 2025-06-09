package com.example.luvisluvproject.domain.tag.entity;

import com.example.luvisluvproject.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
	name = "member_tag",
	uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "tag_id"})
)
public class MemberTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id", nullable = false)
	private Tag tag;

	public MemberTag(Member member, Tag tag) {
		this.member = member;
		this.tag = tag;
	}
}

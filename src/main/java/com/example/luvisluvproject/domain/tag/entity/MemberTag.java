package com.example.luvisluvproject.domain.tag.entity;

import com.example.luvisluvproject.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

/**
 * 회원과 태그 간의 다대다 관계 매핑 테이블
 */
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

	/**
	 * 연결된 사용자
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	/**
	 * 연결된 태그
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id", nullable = false)
	private Tag tag;

	// 생성자 방식 사용 시 필요한 생성자
	public MemberTag(Member member, Tag tag) {
		this.member = member;
		this.tag = tag;
	}
}

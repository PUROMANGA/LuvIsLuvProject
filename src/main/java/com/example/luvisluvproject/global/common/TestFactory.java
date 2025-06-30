package com.example.luvisluvproject.global.common;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.luvisluvproject.domain.block.entity.Block;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.domain.notify.entity.Notify;
import com.example.luvisluvproject.domain.review.entity.Review;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class TestFactory {

	private final PasswordEncoder passwordEncoder;

	/**
	 * 로그인용 멤버 테스트 객체 생성자
	 * @param email
	 * @param pw
	 * @return
	 */

	public Member loginTestOf(String name, String email, String pw) {
		return new Member(
			name,
			email,
			passwordEncoder.encode(pw),
			LocalDate.of(2000, 1, 1),
			UserRole.USER,
			"내용",
			false,
			0,
			0L,
			0);
	}

	/**
	 * 로그인용 멤버 아이디 설정
	 * @param member
	 * @param newId
	 * @return
	 */

	public Member forTestUpdateId(Member member, Long newId) {
		try {
			Field idField = Member.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(member, newId);
			return member;
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException("ID 필드 설정 중 오류 발생 : ", e);
		}
	}

	/**
	 * 블록 테스트 객체 생성자
	 * @param blocker
	 * @param blocked
	 * @return
	 */
	public Block blockTestOf(Member blocker, Member blocked) {
		return new Block(blocker, blocked);
	}

	/**
	 * 알람 테스트 객체 생성자
	 * @param notifyDto
	 * @param receiver
	 * @param sender
	 * @return
	 */
	public Notify notifyTestOf(NotifyDto notifyDto, Member receiver, Member sender) {
		return new Notify(notifyDto, receiver, sender);
	}

	public Review reviewTestOf(Store store, Member member, int rating, String content) {
		return new Review(store, member, rating, content);
	}

	public Tag tagTestOf(String name, TagCategory tagCategory) {
		return new Tag(name, tagCategory);
	}

	public TagRequestDto tagRequestDtoTestOf(String name, TagCategory tagCategory) {
		return new TagRequestDto(name, tagCategory);
	}

	public MemberTag memberTagTestOf(Member member, Tag tag) {
		return new MemberTag(member.getId(), tag.getName(), tag.getCategory());
	}
}

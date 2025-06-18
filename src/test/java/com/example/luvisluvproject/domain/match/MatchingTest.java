package com.example.luvisluvproject.domain.match;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.chat.repository.ChatRoomRepository;
import com.example.luvisluvproject.domain.match.dto.MatchMemberDto;
import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.match.service.MatchService;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j

public class MatchingTest {

	@Autowired
	private MatchService matchService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberTagRepository memberTagRepository;

	@Autowired
	private TagJpaRepository tagJpaRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@BeforeEach
	public void setUp() {
		chatRoomRepository.deleteAll();
		memberRepository.deleteAll();
		tagJpaRepository.deleteAll();
		memberTagRepository.deleteAll();

		Member me = new Member(
			"송진영",
			"songjinyong@email.com",
			"test1234",
			LocalDate.parse("2001-01-01"),
			UserRole.USER,
			true,
			1L
		);

		memberRepository.save(me);

		Tag tag = new Tag(
			"축구",
			TagCategory.HOBBY,
			TagCreatedByType.USER,
			true
		);

		tagJpaRepository.save(tag);

		MemberTag memberTag = new MemberTag(me, tag);

		memberTagRepository.save(memberTag);

		for (int i = 0; i < 999; i++) {
			Member member = new Member(
				"송진영" + i,
				"songjinyong" + i + "@email.com",
				"test1234" + i,
				LocalDate.parse("2001-01-01"),
				UserRole.USER,
				true,
				1L
			);

			Tag tag1 = new Tag(
				"축구" + i,
				TagCategory.HOBBY,
				TagCreatedByType.USER,
				true
			);

			memberRepository.save(member);
			tagJpaRepository.save(tag1);
			MemberTag memberTag1 = new MemberTag(me, tag1);
			MemberTag memberTag2 = new MemberTag(member, tag1);
			memberTagRepository.save(memberTag1);
			memberTagRepository.save(memberTag2);
		}
	}

	@Transactional
	@Test
	@DisplayName("매칭 해주기")
	public void testMatchingCase1() {
		long start = System.currentTimeMillis();
		String email = "songjinyong@email.com";
		Member me = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.USER_CANT_FIND));
		List<MemberTag> memberTagList = memberTagRepository.findAllByMemberId(me.getId());
		List<String> tagsName = new ArrayList<>();
		List<Member> matches = new ArrayList<>();
		//태그 꺼내서 이름만 별도로 저장
		for (MemberTag memberTag : memberTagList) {
			String tagName = memberTag.getTag().getName();
			tagsName.add(tagName);
		}

		for (String tagName : tagsName) {
			List<Member> memberList = memberTagRepository.findAllMemberByTag(tagName);
			for (Member member : memberList) {
				matches.add(member);
			}
		}

		assertThat(matches).isNotNull();
		long end = System.currentTimeMillis();
		log.info("매칭 된 사람들: {}", matches.toString());
		log.info("실행 시간: {} ms", (end - start));
	}

	@Transactional
	@Test
	@DisplayName("매칭 해주기 쿼리 DSL")
	public void testMatchingCase2() {
		long start = System.currentTimeMillis();
		String email = "songjinyong@email.com";
		List<ResponseMatchMemberDto> matchMemberDtoList = memberTagRepository.findResponseMatchMemberDtoFindByEmail(
			email);
		assertThat(matchMemberDtoList).isNotNull();
		long end = System.currentTimeMillis();
		log.info("매칭 된 사람들: {}", matchMemberDtoList.toString());
		log.info("실행 시간: {} ms", (end - start));
	}
}

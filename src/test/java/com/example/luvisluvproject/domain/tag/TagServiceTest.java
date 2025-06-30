// package com.example.luvisluvproject.domain.tag;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
//
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.test.util.ReflectionTestUtils;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.example.luvisluvproject.domain.member.entity.Member;
// import com.example.luvisluvproject.domain.member.repository.MemberRepository;
// import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
// import com.example.luvisluvproject.domain.tag.entity.MemberTag;
// import com.example.luvisluvproject.domain.tag.entity.Tag;
// import com.example.luvisluvproject.domain.tag.enums.TagCategory;
// import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
// import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;
// import com.example.luvisluvproject.global.common.TestFactory;
//
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @SpringBootTest
// public class TagServiceTest {
//
// 	@Autowired
// 	private TagJpaRepository tagJpaRepository;
//
// 	@Autowired
// 	private MemberRepository memberRepository;
//
// 	@Autowired
// 	private MemberTagRepository memberTagRepository;
//
// 	@Autowired
// 	private RedisTemplate<String, Tag> tagRedisTemplate;
//
// 	@Autowired
// 	private TestFactory testFactory;
//
// 	@Autowired
// 	private PasswordEncoder passwordEncoder;
//
// 	List<TagRequestDto> tagList = new ArrayList<>();
//
// 	@BeforeEach
// 	void setUp() {
// 		Member member = testFactory.loginTestOf("박지성", "park@test.com", "test1234");
// 		Member anotherMember = testFactory.loginTestOf("김준석", "kim@test.com", "test1234");
// 		memberRepository.save(member);
// 		memberRepository.save(anotherMember);
//
// 		Tag tag = testFactory.tagTestOf("테스트 태그", TagCategory.GENDER_IDENTITY);
//
// 		MemberTag memberTag = testFactory.memberTagTestOf(member, tag);
// 		MemberTag anotherMemberTag = testFactory.memberTagTestOf(anotherMember, tag);
// 		ReflectionTestUtils.setField(member, "tagCount", 18);
// 		ReflectionTestUtils.setField(anotherMember, "tagCount", 18);
//
// 		for(int i = 0; i < 3000; i++) {
// 			TagRequestDto savedTag = testFactory.tagRequestDtoTestOf("테스트 태그" + i, TagCategory.GENDER_IDENTITY);
// 			tagList.add(savedTag);
// 		}
// 	}
//
// 	@AfterEach
// 	void cleanUp() {
// 		memberTagRepository.deleteAll();
// 		memberRepository.deleteAll();
// 	}
//
// 	@Transactional
// 	@Test
// 	@DisplayName("1번 케이스")
// 	void syncTagsTestCase1() {
//
// 		String email = "park@test.com";
//
// 		Member me = memberRepository.findByEmail(email)
// 			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//
// 		long start = System.currentTimeMillis();
//
// 		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// 			int countingMemberTag = memberRepository.findTagCountById(me.getId());
//
// 			if (countingMemberTag < 30) {
// 				for (TagRequestDto requestDto : tagList) {
//
// 					Tag tag = requestDto.toTag(requestDto.getName(), requestDto.getCategory());
//
// 					MemberTag memberTag = new MemberTag(me.getId(), tag.getId());
// 					memberTagRepository.save(memberTag);
// 					me.plusTagCount();
//
// 					int countingAfterSavedMemberTag = memberRepository.findTagCountById(me.getId());
//
// 					if (countingAfterSavedMemberTag > 30) {
// 						throw new RuntimeException("태그는 30개 이상 가질 수 없습니다.");
// 					}
// 				}
// 			}
// 		});
// 		assertEquals("태그는 30개 이상 가질 수 없습니다.", exception.getMessage());
// 		long end = System.currentTimeMillis();
// 		log.info("케이스 1의 실행 시간: {} ms", (end - start));
// 	}
//
// 	@Transactional
// 	@Test
// 	@DisplayName("2번 케이스")
// 	void syncTagsTestCase2() {
//
// 		String email = "park@test.com";
//
// 		Member me = memberRepository.findByEmail(email)
// 			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//
// 		List<Tag> tags = new ArrayList<>();
//
// 		long start = System.currentTimeMillis();
//
// 		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// 			int countingMemberTag = memberRepository.findTagCountById(me.getId());
//
// 			if (countingMemberTag < 30) {
// 				for (TagRequestDto requestDto : tagList) {
//
// 					Tag tag = requestDto.toTag(requestDto.getName(), requestDto.getCategory());
// 					tags.add(tag);
// 				}
// 			}
//
// 			for(Tag tag : tags) {
// 				MemberTag memberTag = new MemberTag(me.getId(), tag.getId());
// 				memberTagRepository.save(memberTag);
// 				me.plusTagCount();
//
// 				int countingAfterSavedMemberTag = memberRepository.findTagCountById(me.getId());
//
// 				if (countingAfterSavedMemberTag > 30) {
// 					throw new RuntimeException("태그는 30개 이상 가질 수 없습니다.");
// 				}
// 			}
// 		});
//
// 		assertEquals("태그는 30개 이상 가질 수 없습니다.", exception.getMessage());
// 		long end = System.currentTimeMillis();
// 		log.info("케이스 2의 실행 시간: {} ms", (end - start));
// 	}
//
// 	@Transactional
// 	@Test
// 	@DisplayName("3번 케이스")
// 	void syncTagsTestCase3() {
//
// 		String email = "park@test.com";
//
// 		Member me = memberRepository.findByEmail(email)
// 			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//
// 		List<Tag> tags = new ArrayList<>();
//
// 		long start = System.currentTimeMillis();
//
// 		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//
// 			int countingMemberTag = memberRepository.findTagCountById(me.getId());
//
// 			if (countingMemberTag < 30) {
// 				for (TagRequestDto requestDto : tagList) {
//
// 					Tag tag = requestDto.toTag(requestDto.getName(), requestDto.getCategory());
// 					tags.add(tag);
// 				}
// 			}
//
// 			for(Tag tag : tags) {
// 				MemberTag memberTag = new MemberTag(me.getId(), tag.getId());
// 				memberTagRepository.save(memberTag);
// 				me.plusTagCount();
// 			}
//
// 			int countingAfterSavedMemberTag = memberRepository.findTagCountById(me.getId());
//
// 			if (countingAfterSavedMemberTag > 30) {
// 				throw new RuntimeException("태그는 30개 이상 가질 수 없습니다.");
// 			}
// 		});
//
// 		assertEquals("태그는 30개 이상 가질 수 없습니다.", exception.getMessage());
// 		long end = System.currentTimeMillis();
// 		log.info("케이스 3의 실행 시간: {} ms", (end - start));
// 	}
// }

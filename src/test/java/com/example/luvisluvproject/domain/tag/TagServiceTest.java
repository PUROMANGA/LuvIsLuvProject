package com.example.luvisluvproject.domain.tag;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.document.TagDocument;
import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;
import com.example.luvisluvproject.domain.tag.repository.TagSearchRepository;
import com.example.luvisluvproject.domain.tag.service.TagService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

public class TagServiceTest {

	@Mock
	private TagJpaRepository tagJpaRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private MemberTagRepository memberTagRepository;
	@Mock
	private TagSearchRepository tagSearchRepository;

	@InjectMocks
	private TagService tagService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void 태그_생성() {
		TagRequestDto dto = new TagRequestDto("독서", "취미", TagCreatedByType.USER, true, 1);
		Tag tag = Tag.builder().id(1L).name("독서").category("취미").createdByType(TagCreatedByType.USER).active(true).priority(1).build();

		given(tagJpaRepository.save(any(Tag.class))).willReturn(tag);

		var result = tagService.createTag(dto);

		assertThat(result.getName()).isEqualTo("독서");
		assertThat(result.getCategory()).isEqualTo("취미");
	}

	@Test
	void 태그_수정() {
		Long tagId = 1L;
		Tag origin = Tag.builder().id(tagId).name("독서").category("취미").createdByType(TagCreatedByType.USER).active(true).priority(1).build();
		TagRequestDto updateDto = new TagRequestDto("요리", "취미", TagCreatedByType.ADMIN, false, 2);

		given(tagJpaRepository.findById(tagId)).willReturn(Optional.of(origin));
		given(tagJpaRepository.save(any(Tag.class))).willAnswer(invocation -> invocation.getArgument(0));

		var result = tagService.updateTag(tagId, updateDto);

		assertThat(result.getName()).isEqualTo("요리");
		assertThat(result.getCreatedByType()).isEqualTo(TagCreatedByType.ADMIN);
	}

	@Test
	void 태그_삭제() {
		Long tagId = 1L;
		Tag tag = Tag.builder().id(tagId).build();

		given(tagJpaRepository.findById(tagId)).willReturn(Optional.of(tag));
		willDoNothing().given(tagJpaRepository).delete(tag);

		tagService.deleteTag(tagId);

		then(tagJpaRepository).should().delete(tag);
	}

	@Test
	void 자동완성_검색() {
		String keyword = "독";
		TagDocument doc = new TagDocument(1L, "독서", "취미", TagCreatedByType.USER, true, 1);

		given(tagSearchRepository.searchByName(keyword)).willReturn(List.of(doc));

		var result = tagService.searchTags(keyword);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("독서");
	}

	@Test
	void 회원_태그_할당() {
		Long memberId = 1L;
		Member member = Member.builder().id(memberId).build();
		Tag tag = Tag.builder().id(1L).name("운동").build();

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(tagJpaRepository.findAllById(List.of(1L))).willReturn(List.of(tag));
		willDoNothing().given(memberTagRepository).deleteByMemberId(memberId);

		tagService.assignTagsToMember(memberId, List.of(1L));

		then(memberTagRepository).should().saveAll(any());
	}

	@Test
	void 회원_태그_조회() {
		Long memberId = 1L;
		Tag tag = Tag.builder().id(1L).name("영화").category("취미").build();
		MemberTag mt = MemberTag.builder().tag(tag).build();

		given(memberTagRepository.findAllByMemberId(memberId)).willReturn(List.of(mt));

		var result = tagService.getTagsByMemberId(memberId);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getName()).isEqualTo("영화");
	}

	@Test
	void 존재하지_않는_회원이면_예외() {
		given(memberRepository.findById(99L)).willReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class,
			() -> tagService.assignTagsToMember(99L, List.of(1L)));
	}
}

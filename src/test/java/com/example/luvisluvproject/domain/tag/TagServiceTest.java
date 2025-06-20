package com.example.luvisluvproject.domain.tag;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.tag.document.TagDocument;
import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.dto.TagResponseDto;
import com.example.luvisluvproject.domain.tag.entity.MemberTag;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;
import com.example.luvisluvproject.domain.tag.repository.MemberTagRepository;
import com.example.luvisluvproject.domain.tag.repository.TagJpaRepository;
import com.example.luvisluvproject.domain.tag.repository.TagSearchRepository;
import com.example.luvisluvproject.domain.tag.service.TagService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

	@Mock private TagJpaRepository tagJpaRepository;
	@Mock private MemberRepository memberRepository;
	@Mock private MemberTagRepository memberTagRepository;
	@Mock private TagSearchRepository tagSearchRepository;
	@InjectMocks private TagService tagService;

	private TagRequestDto requestDto;
	private Member member;

	@BeforeEach
	void setUp() {
		requestDto = new TagRequestDto("독서", "HOBBY", TagCreatedByType.USER, true, 3);
		member = Member.builder()
			.id(1L)
			.name("테스터")
			.email("test@email.com")
			.password("pass")
			.birthday(LocalDate.of(1990, 1, 1))
			.status(true)
			.build();
	}

	@Test
	void 태그를_생성한다() {
		Tag expected = Tag.builder()
			.id(10L)
			.name("독서")
			.category(TagCategory.HOBBY)
			.createdByType(TagCreatedByType.USER)
			.active(true)
			.priority(3)
			.build();

		given(tagJpaRepository.save(any(Tag.class))).willReturn(expected);

		TagResponseDto result = tagService.createTag(member, requestDto);

		assertThat(result.getName()).isEqualTo("독서");
		assertThat(result.getCategory()).isEqualTo(TagCategory.HOBBY);
		assertThat(result.getCreatedByType()).isEqualTo(TagCreatedByType.USER);
		assertThat(result.getPriority()).isEqualTo(3);
	}

	@Test
	void 태그를_업데이트한다() {
		Tag tag = Tag.builder()
			.id(10L)
			.name("기존태그")
			.category(TagCategory.HOBBY)
			.createdByType(TagCreatedByType.ADMIN)
			.priority(1)
			.active(true)
			.build();

		given(tagJpaRepository.findById(10L)).willReturn(Optional.of(tag));
		given(tagJpaRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

		TagRequestDto updateDto = new TagRequestDto("수정태그", "HOBBY", TagCreatedByType.USER, false, 7);
		TagResponseDto result = tagService.updateTag(10L, updateDto);

		assertThat(result.getName()).isEqualTo("수정태그");
		assertThat(result.getCreatedByType()).isEqualTo(TagCreatedByType.USER);
		assertThat(result.getActive()).isFalse();
	}

	@Test
	void 존재하지않는_태그_업데이트시_예외발생() {
		given(tagJpaRepository.findById(99L)).willReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> tagService.updateTag(99L, requestDto));
	}

	@Test
	void 태그를_삭제한다() {
		Tag tag = Tag.builder().id(5L).name("삭제할 태그").build();
		given(tagJpaRepository.findById(5L)).willReturn(Optional.of(tag));
		willDoNothing().given(tagJpaRepository).delete(tag);

		tagService.deleteTag(5L);

		then(tagJpaRepository).should().delete(tag);
	}

	@Test
	void 존재하지않는_태그_삭제시_예외발생() {
		given(tagJpaRepository.findById(5L)).willReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> tagService.deleteTag(5L));
	}

	@Test
	void 자동완성_검색을_수행한다() {
		TagDocument doc = TagDocument.builder()
			.id(1L)
			.name("독서모임")
			.category("HOBBY")
			.createdByType(TagCreatedByType.USER)
			.priority(2)
			.active(true)
			.build();

		Slice<TagDocument> docs = new SliceImpl<>(List.of(doc), PageRequest.of(0, 10), false);
		given(tagSearchRepository.searchByName("독서", PageRequest.of(0, 10))).willReturn(docs);

		Slice<TagResponseDto> result = tagService.searchTags("독서", PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getName()).contains("독서");
	}

	@Test
	void 회원에게_태그를_연결한다() {
		List<Long> tagIds = List.of(1L, 2L);
		Tag tag1 = Tag.builder().id(1L).name("독서").build();
		Tag tag2 = Tag.builder().id(2L).name("여행").build();

		given(memberRepository.findById(1L)).willReturn(Optional.of(member));
		given(tagJpaRepository.findAllById(tagIds)).willReturn(List.of(tag1, tag2));
		willDoNothing().given(memberTagRepository).deleteByMemberId(1L);
		given(memberTagRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

		Slice<TagResponseDto> result = tagService.assignTagsToMember(1L, tagIds, PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(2);
	}

	@Test
	void 없는_회원에게_태그_연결시_예외() {
		given(memberRepository.findById(999L)).willReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () ->
			tagService.assignTagsToMember(999L, List.of(1L), PageRequest.of(0, 10)));
	}

	@Test
	void 회원의_태그목록을_조회한다() {
		Tag tag = Tag.builder().id(1L).name("독서").category(TagCategory.HOBBY).build();
		MemberTag mt = MemberTag.builder().member(member).tag(tag).build();

		given(memberTagRepository.findAllByMemberId(1L)).willReturn(List.of(mt));

		Slice<TagResponseDto> result = tagService.getTagsByMemberId(1L, PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getName()).isEqualTo("독서");
	}
}

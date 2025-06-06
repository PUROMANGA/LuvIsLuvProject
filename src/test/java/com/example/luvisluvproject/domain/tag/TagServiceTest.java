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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

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

	private TagRequestDto sampleRequestDto;
	private Tag sampleTag;
	private Member sampleMember;

	@BeforeEach
	void setUp() {
		sampleRequestDto = new TagRequestDto(
			"독서",
			"HOBBY",
			TagCreatedByType.USER,
			true,
			5
		);
		sampleTag = Tag.builder()
			.id(1L)
			.name("독서")
			.category(TagCategory.HOBBY)
			.createdByType(TagCreatedByType.USER)
			.active(true)
			.priority(5)
			.build();
		// ID는 builder로 자동 부여되지 않으므로 리플렉션 없이 테스트용도로 setter를 가정하거나, 생성자로 직접 할당
		// 여기서는 빌더 반환 후 리플렉션 대신, Mockito가 save 시 sampleTag를 리턴한다고 가정합니다.

		sampleMember = Member.builder()
			.id(100L)
			.name("테스터")
			.email("test@example.com")
			.password("pass")
			.birthday(LocalDate.of(1990, 1, 1))
			.userRole(null)
			.status(true)
			.build();
	}

	@Test
	void createTag_성공() {
		// given
		given(tagJpaRepository.save(any(Tag.class))).willAnswer(invocation -> {
			Tag arg = invocation.getArgument(0);
			// 저장 시 ID가 붙어서 돌아온다고 가정
			return Tag.builder()
				.id(1L)
				.name(arg.getName())
				.category(arg.getCategory())
				.createdByType(arg.getCreatedByType())
				.active(arg.isActive())
				.priority(arg.getPriority())
				.build();
		});

		// when
		TagResponseDto result = tagService.createTag(sampleRequestDto);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("독서");
		assertThat(result.getCategory()).isEqualTo(TagCategory.HOBBY);
		assertThat(result.getCreatedByType()).isEqualTo(TagCreatedByType.USER);
		assertThat(result.getActive()).isTrue();
		assertThat(result.getPriority()).isEqualTo(5);

		then(tagJpaRepository).should().save(any(Tag.class));
	}

	@Test
	void updateTag_존재하는태그_성공() {
		// given
		given(tagJpaRepository.findById(1L)).willReturn(Optional.of(sampleTag));
		given(tagJpaRepository.save(any(Tag.class))).willAnswer(invocation -> invocation.getArgument(0));

		TagRequestDto updateDto = new TagRequestDto(
			"캠핑",
			"HOBBY",
			TagCreatedByType.ADMIN,
			false,
			10
		);

		// when
		TagResponseDto result = tagService.updateTag(1L, updateDto);

		// then
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("캠핑");
		assertThat(result.getCategory()).isEqualTo(TagCategory.HOBBY);
		assertThat(result.getCreatedByType()).isEqualTo(TagCreatedByType.ADMIN);
		assertThat(result.getActive()).isFalse();
		assertThat(result.getPriority()).isEqualTo(10);

		then(tagJpaRepository).should().findById(1L);
		then(tagJpaRepository).should().save(any(Tag.class));
	}

	@Test
	void updateTag_존재하지않는태그_예외() {
		// given
		given(tagJpaRepository.findById(999L)).willReturn(Optional.empty());

		// when & then
		assertThrows(IllegalArgumentException.class, () -> tagService.updateTag(999L, sampleRequestDto));

		then(tagJpaRepository).should().findById(999L);
		then(tagJpaRepository).should(never()).save(any());
	}

	@Test
	void deleteTag_존재하는태그_성공() {
		// given
		given(tagJpaRepository.findById(1L)).willReturn(Optional.of(sampleTag));
		willDoNothing().given(tagJpaRepository).delete(sampleTag);

		// when
		tagService.deleteTag(1L);

		// then
		then(tagJpaRepository).should().findById(1L);
		then(tagJpaRepository).should().delete(sampleTag);
	}

	@Test
	void deleteTag_존재하지않는태그_예외() {
		// given
		given(tagJpaRepository.findById(123L)).willReturn(Optional.empty());

		// when & then
		assertThrows(IllegalArgumentException.class, () -> tagService.deleteTag(123L));

		then(tagJpaRepository).should().findById(123L);
		then(tagJpaRepository).should(never()).delete(any());
	}

	@Test
	void searchTags_자동완성검색() {
		// given
		TagDocument doc = TagDocument.builder()
			.id(5L)
			.name("독서토론")
			.category("HOBBY")
			.createdByType(TagCreatedByType.USER)
			.active(true)
			.priority(3)
			.build();

		List<TagDocument> docs = Collections.singletonList(doc);
		Pageable pageable = PageRequest.of(0, 10);
		Slice<TagDocument> docSlice = new SliceImpl<>(docs, pageable, false);

		given(tagSearchRepository.searchByName("독서", pageable)).willReturn(docSlice);

		// when
		Slice<TagResponseDto> resultSlice = tagService.searchTags("독서", pageable);

		// then
		assertThat(resultSlice.getContent()).hasSize(1);
		TagResponseDto dto = resultSlice.getContent().get(0);
		assertThat(dto.getId()).isEqualTo(5L);
		assertThat(dto.getName()).isEqualTo("독서토론");
		assertThat(dto.getCategory()).isEqualTo(TagCategory.HOBBY);
		assertThat(dto.getCreatedByType()).isEqualTo(TagCreatedByType.USER);
		assertThat(dto.getActive()).isTrue();
		assertThat(dto.getPriority()).isEqualTo(3);

		then(tagSearchRepository).should().searchByName("독서", pageable);
	}

	@Test
	void assignTagsToMember_성공() {
		// given
		Long memberId = 100L;
		List<Long> tagIds = Arrays.asList(1L, 2L);

		Tag tag1 = Tag.builder()
			.id(1L)
			.name("독서")
			.category(TagCategory.HOBBY)
			.createdByType(TagCreatedByType.USER)
			.active(true)
			.priority(1)
			.build();

		Tag tag2 = Tag.builder()
			.id(2L)
			.name("영화")
			.category(TagCategory.HOBBY)
			.createdByType(TagCreatedByType.USER)
			.active(true)
			.priority(2)
			.build();

		given(memberRepository.findById(memberId)).willReturn(Optional.of(sampleMember));
		// deleteByMemberId는 void 메서드 → doNothing
		willDoNothing().given(memberTagRepository).deleteByMemberId(memberId);
		given(tagJpaRepository.findAllById(tagIds)).willReturn(Arrays.asList(tag1, tag2));
		given(memberTagRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Slice<TagResponseDto> resultSlice = tagService.assignTagsToMember(memberId, tagIds, pageable);

		// then
		assertThat(resultSlice.getContent()).hasSize(2);
		List<String> names = Arrays.asList(
			resultSlice.getContent().get(0).getName(),
			resultSlice.getContent().get(1).getName()
		);
		assertThat(names).containsExactlyInAnyOrder("독서", "영화");

		then(memberRepository).should().findById(memberId);
		then(memberTagRepository).should().deleteByMemberId(memberId);
		then(tagJpaRepository).should().findAllById(tagIds);
		then(memberTagRepository).should().saveAll(anyList());
	}

	@Test
	void assignTagsToMember_회원없음_예외() {
		// given
		Long memberId = 999L;
		given(memberRepository.findById(memberId)).willReturn(Optional.empty());

		// when & then
		assertThrows(IllegalArgumentException.class, () -> tagService.assignTagsToMember(memberId, Collections.singletonList(1L), PageRequest.of(0, 10)));

		then(memberRepository).should().findById(memberId);
		then(memberTagRepository).should(never()).deleteByMemberId(anyLong());
		then(tagJpaRepository).should(never()).findAllById(anyList());
	}

	@Test
	void getTagsByMemberId_성공() {
		// given
		Long memberId = 100L;

		Tag tag1 = Tag.builder()
			.id(1L)
			.name("독서")
			.category(TagCategory.HOBBY)
			.createdByType(TagCreatedByType.USER)
			.active(true)
			.priority(1)
			.build();

		MemberTag mt1 = MemberTag.builder()
			.id(10L)
			.member(sampleMember)
			.tag(tag1)
			.build();

		List<MemberTag> memberTags = Collections.singletonList(mt1);
		given(memberTagRepository.findAllByMemberId(memberId)).willReturn(memberTags);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Slice<TagResponseDto> resultSlice = tagService.getTagsByMemberId(memberId, pageable);

		// then
		assertThat(resultSlice.getContent()).hasSize(1);
		TagResponseDto dto = resultSlice.getContent().get(0);
		assertThat(dto.getId()).isEqualTo(1L);
		assertThat(dto.getName()).isEqualTo("독서");
		assertThat(dto.getCategory()).isEqualTo(TagCategory.HOBBY);

		then(memberTagRepository).should().findAllByMemberId(memberId);
	}

	@Test
	void getTagsByMemberId_태그없음() {
		// given
		Long memberId = 200L;
		given(memberTagRepository.findAllByMemberId(memberId)).willReturn(Collections.emptyList());

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Slice<TagResponseDto> resultSlice = tagService.getTagsByMemberId(memberId, pageable);

		// then
		assertThat(resultSlice.getContent()).isEmpty();
		then(memberTagRepository).should().findAllByMemberId(memberId);
	}
}

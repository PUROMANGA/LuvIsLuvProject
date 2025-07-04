package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.review.dto.ReviewCreateResponseDto;
import com.example.luvisluvproject.domain.tag.dto.TagRequestDto;
import com.example.luvisluvproject.domain.tag.entity.Tag;
import com.example.luvisluvproject.domain.tag.enums.TagCategory;
import com.example.luvisluvproject.domain.tag.service.TagService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class TagFlowTest {

	@Autowired
	private TagService tagService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	@Qualifier("tagRedisTemplate")
	private RedisTemplate<String, Tag> tagRedisTemplate;

	@Test
	@DisplayName("금지 단어 태그 테스트")
	void 금지_단어_태그_테스트() {
		String meEmail = "test1234@email.com";

		TagRequestDto tagRequestDto = new TagRequestDto(
			"미친 축구", TagCategory.HOBBY
		);

		TagRequestDto tagRequestDto2 = new TagRequestDto(
			"미친 게이", TagCategory.SEXUAL_ORIENTATION
		);

		TagRequestDto tagRequestDto3 = new TagRequestDto(
			"미친 여성", TagCategory.GENDER_IDENTITY
		);

		List<TagRequestDto> tagRequestDtoList = new ArrayList<>();

		tagRequestDtoList.add(tagRequestDto);
		tagRequestDtoList.add(tagRequestDto2);
		tagRequestDtoList.add(tagRequestDto3);

		RuntimeException e = assertThrows(RuntimeException.class, () -> {
			tagService.syncTags(tagRequestDtoList, meEmail);
		});

		assertThat(e.getMessage()).isEqualTo("금지단어가 포함되어 있습니다.");
	}

	@Test
	@DisplayName("멤버 태그 30개이상이라 실패하는 테스트")
	void 멤버_태그_30개이상_테스트() {
		String meEmail = "test1234@email.com";

		Member me = memberRepository.findByEmail(meEmail)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		ReflectionTestUtils.setField(me, "tagCount", 29);

		TagRequestDto tagRequestDto = new TagRequestDto(
			"축구", TagCategory.HOBBY
		);

		TagRequestDto tagRequestDto2 = new TagRequestDto(
			"게이", TagCategory.SEXUAL_ORIENTATION
		);

		TagRequestDto tagRequestDto3 = new TagRequestDto(
			"여성", TagCategory.GENDER_IDENTITY
		);

		List<TagRequestDto> tagRequestDtoList = new ArrayList<>();

		tagRequestDtoList.add(tagRequestDto);
		tagRequestDtoList.add(tagRequestDto2);
		tagRequestDtoList.add(tagRequestDto3);

		RuntimeException e = assertThrows(RuntimeException.class, () -> {
			tagService.syncTags(tagRequestDtoList, meEmail);
		});

		assertThat(e.getMessage()).isEqualTo("태그는 30개 이상 가질 수 없습니다.");
	}

	@Test
	@DisplayName("태그 작성 테스트")
	void 태그_작성_테스트() {

		tagRedisTemplate.delete("Tag");
		String meEmail = "test1234@email.com";

		TagRequestDto tagRequestDto = new TagRequestDto(
			"축구", TagCategory.HOBBY
		);

		TagRequestDto tagRequestDto2 = new TagRequestDto(
			"게이", TagCategory.SEXUAL_ORIENTATION
		);

		TagRequestDto tagRequestDto3 = new TagRequestDto(
			"상여자", TagCategory.GENDER_IDENTITY
		);

		List<TagRequestDto> tagRequestDtoList = new ArrayList<>();

		tagRequestDtoList.add(tagRequestDto);
		tagRequestDtoList.add(tagRequestDto2);
		tagRequestDtoList.add(tagRequestDto3);

		tagService.syncTags(tagRequestDtoList, meEmail);
		ApiResponse<ReviewCreateResponseDto> apiResponse = ApiResponse.of(SuccessCode.CREATE_TAG_REQUEST_SUCCESS, null);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("태그 요청 성공");
	}
}

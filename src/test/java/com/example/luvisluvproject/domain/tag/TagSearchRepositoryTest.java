package com.example.luvisluvproject.domain.tag;

import com.example.luvisluvproject.domain.tag.document.TagDocument;
import com.example.luvisluvproject.domain.tag.enums.TagCreatedByType;
import com.example.luvisluvproject.domain.tag.repository.TagSearchRepository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TagSearchRepositoryTest {

	@Autowired
	private TagSearchRepository tagSearchRepository;

	private final Pageable pageable = PageRequest.of(0, 10);

	private final List<TagDocument> sampleDocs = Arrays.asList(
		TagDocument.builder()
			.id(1L)
			.name("독서")
			.category("HOBBY")
			.createdByType(TagCreatedByType.USER)
			.active(true)
			.priority(1)
			.build(),
		TagDocument.builder()
			.id(2L)
			.name("독서토론")
			.category("HOBBY")
			.createdByType(TagCreatedByType.ADMIN)
			.active(true)
			.priority(2)
			.build()
	);

	@BeforeEach
	void setUp() {
		tagSearchRepository.deleteIndex();
		tagSearchRepository.createIndex();
		tagSearchRepository.saveAll(sampleDocs);
		// Elasticsearch는 실시간 검색이 보장되지 않으므로 약간의 대기 필요
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignored) {}
	}

	@AfterEach
	void tearDown() {
		tagSearchRepository.deleteIndex();
	}

	@Test
	@Order(1)
	void searchByName_정상검색() {
		// when
		Slice<TagDocument> result = tagSearchRepository.searchByName("독서", pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent()).extracting("name").contains("독서", "독서토론");
	}

	@Test
	@Order(2)
	void searchByName_일치없음() {
		// when
		Slice<TagDocument> result = tagSearchRepository.searchByName("수영", pageable);

		// then
		assertThat(result.getContent()).isEmpty();
	}
}

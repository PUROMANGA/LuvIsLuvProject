package com.example.luvisluvproject.domain.tag.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.example.luvisluvproject.domain.tag.document.TagDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Elasticsearch 기반 태그 검색용 Repository
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TagSearchRepository {

	private final ElasticsearchClient elasticsearchClient;

	private static final String INDEX_NAME = "tags";

	/**
	 * 자동완성 검색 - prefix 기반 검색 (EdgeNGram 지원)
	 */
	public Slice<TagDocument> searchByName(String keyword, Pageable pageable) {
		try {
			int from = (int) pageable.getOffset();
			int size = pageable.getPageSize() + 1;  // hasNext 확인용

			SearchResponse<TagDocument> response = elasticsearchClient.search(s -> s
				.index(INDEX_NAME)
				.from(from)
				.size(size)
				.query(q -> q
					.prefix(p -> p
						.field("name")
						.value(keyword)
					)
				), TagDocument.class
			);

			List<TagDocument> content = response.hits().hits().stream()
				.map(Hit::source)
				.collect(Collectors.toList());

			boolean hasNext = content.size() > pageable.getPageSize();
			if (hasNext) content = content.subList(0, pageable.getPageSize());

			return new SliceImpl<>(content, pageable, hasNext);

		} catch (IOException e) {
			log.error("[Elasticsearch] 태그 검색 실패: {}", keyword, e);
			throw new RuntimeException("Elasticsearch 검색 오류", e);
		}
	}

	/**
	 * 테스트용 - 인덱스 전체 삭제
	 */
	public void deleteIndex() {
		try {
			elasticsearchClient.indices().delete(d -> d.index(INDEX_NAME));
		} catch (Exception e) {
			log.warn("[Elasticsearch] 인덱스 삭제 실패 (무시): {}", e.getMessage());
		}
	}

	/**
	 * 테스트용 - 인덱스 생성
	 */
	public void createIndex() {
		try {
			elasticsearchClient.indices().create(CreateIndexRequest.of(c -> c.index(INDEX_NAME)));
		} catch (IOException e) {
			log.error("[Elasticsearch] 인덱스 생성 실패", e);
		}
	}

	/**
	 * 테스트용 - 다건 문서 저장
	 */
	public void saveAll(List<TagDocument> documents) {
		for (TagDocument doc : documents) {
			try {
				IndexResponse response = elasticsearchClient.index(i -> i
					.index(INDEX_NAME)
					.id(String.valueOf(doc.getId()))
					.document(doc)
				);
				log.debug("[Elasticsearch] 저장 성공: {}", response.result());
			} catch (IOException e) {
				log.error("[Elasticsearch] 저장 실패 - {}: {}", doc.getName(), e.getMessage());
			}
		}
	}
}

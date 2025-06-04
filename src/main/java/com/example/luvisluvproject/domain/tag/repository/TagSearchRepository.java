package com.example.luvisluvproject.domain.tag.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.luvisluvproject.domain.tag.document.TagDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TagSearchRepository {

	private final ElasticsearchClient elasticsearchClient;

	/**
	 * 엘라스틱서치에서 name 필드로 자동완성 검색
	 * @param keyword 입력 키워드
	 * @return 매칭되는 태그 문서 리스트
	 */
	public List<TagDocument> searchByName(String keyword) {
		try {
			SearchResponse<TagDocument> response = elasticsearchClient.search(s -> s
					.index("tags")
					.query(q -> q
						.match(m -> m
							.field("name")
							.query(keyword)
						)
					)
					.size(10),
				TagDocument.class
			);

			return response.hits().hits().stream()
				.map(Hit::source)
				.collect(Collectors.toList());

		} catch (IOException e) {
			log.error("[Elasticsearch] 태그 자동완성 검색 실패: keyword = {}", keyword, e);
			throw new RuntimeException("엘라스틱서치 태그 검색 중 오류 발생: " + keyword, e);
		}
	}
}

package com.example.luvisluvproject.domain.store.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

/**
 * Kakao 주소 검색 API 응답 구조를 담는 클래스
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddressResponse {

	@JsonProperty("documents")
	private List<Document> documents;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Document {
		@JsonProperty("x")
		private String longitude; // 경도

		@JsonProperty("y")
		private String latitude; // 위도
	}

	/**
	 * 첫 번째 문서의 위도를 Optional로 반환
	 *
	 * @return Optional<Double> 형태의 위도 값
	 */
	public Optional<Double> getLatitude() {
		return documents != null && !documents.isEmpty()
			? Optional.ofNullable(documents.get(0))
			.map(Document::getLatitude)
			.map(Double::parseDouble)
			: Optional.empty();
	}

	/**
	 * 첫 번째 문서의 경도를 Optional로 반환
	 *
	 * @return Optional<Double> 형태의 경도 값
	 */
	public Optional<Double> getLongitude() {
		return documents != null && !documents.isEmpty()
			? Optional.ofNullable(documents.get(0))
			.map(Document::getLongitude)
			.map(Double::parseDouble)
			: Optional.empty();
	}
}

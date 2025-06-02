package com.example.luvisluvproject.domain.store.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

import java.util.List;

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
	 * 첫 번째 주소의 위도와 경도를 가져옴
	 */
	public double getLatitude() {
		return documents != null && !documents.isEmpty() ? Double.parseDouble(documents.get(0).getLatitude()) : 0.0;
	}

	public double getLongitude() {
		return documents != null && !documents.isEmpty() ? Double.parseDouble(documents.get(0).getLongitude()) : 0.0;
	}
}

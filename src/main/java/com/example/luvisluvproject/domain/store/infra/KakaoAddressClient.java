package com.example.luvisluvproject.domain.store.infra;

import java.net.URI;

import com.example.luvisluvproject.domain.store.infra.dto.KakaoAddressResponse;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Kakao Local API를 호출하여 주소 → 위도/경도 변환을 수행하는 클라이언트
 */
@Component
@RequiredArgsConstructor
public class KakaoAddressClient {

	private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${KAKAO_API_KEY}")
	private String kakaoApiKey;

	/**
	 * 주소를 기반으로 Kakao API를 호출하여 위도/경도를 반환
	 *
	 * @param address 변환할 주소
	 * @return KakaoAddressResponse 객체 (위도, 경도 포함)
	 * @throws CustomRuntimeException Kakao API 호출 결과가 비어있는 경우 예외 발생
	 */
	public KakaoAddressResponse fetchCoordinates(String address) {
		URI uri = UriComponentsBuilder.fromHttpUrl(KAKAO_API_URL).queryParam("query", address)
			.encode()
			.build()
			.toUri();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + kakaoApiKey);

		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<KakaoAddressResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity,
			KakaoAddressResponse.class);

		if (response.getBody() == null || response.getBody().getDocuments().isEmpty()) {
			throw new CustomRuntimeException(ExceptionCode.KAKAO_API_EMPTY_RESULT);
		}

		return response.getBody();
	}
}

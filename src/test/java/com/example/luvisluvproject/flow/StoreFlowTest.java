package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.luvisluvproject.domain.image.entity.Image;
import com.example.luvisluvproject.domain.image.service.ImageService;
import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
import com.example.luvisluvproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
import com.example.luvisluvproject.domain.store.entity.Store;
import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import com.example.luvisluvproject.domain.store.infra.KakaoAddressClient;
import com.example.luvisluvproject.domain.store.infra.dto.KakaoAddressResponse;
import com.example.luvisluvproject.domain.store.service.StoreService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Import(StoreFlowTest.MockConfig.class)
@Slf4j
public class StoreFlowTest {

	@Autowired
	private StoreService storeService;

	@TestConfiguration
	static class MockConfig {

		@Bean
		public KakaoAddressClient kakaoAddressClient() {
			KakaoAddressClient mock = mock(KakaoAddressClient.class);

			KakaoAddressResponse.Document doc = new KakaoAddressResponse.Document();
			ReflectionTestUtils.setField(doc, "latitude", "37.123456");
			ReflectionTestUtils.setField(doc, "longitude", "127.654321");

			KakaoAddressResponse fakeResponse = new KakaoAddressResponse();
			ReflectionTestUtils.setField(fakeResponse, "documents", List.of(doc));

			given(mock.fetchCoordinates(anyString())).willReturn(fakeResponse);
			when(mock.fetchCoordinates(anyString())).thenReturn(fakeResponse);
			return mock;
		}

		@Bean
		public ImageService imageService() {
			ImageService mock = mock(ImageService.class);
			Store fakeStore = mock(Store.class);

			Image fakeImage = Image.builder()
				.imagePath("https://s3.aws.com/store/fake.jpg")
				.originalName("fake.jpg")
				.store(fakeStore)
				.build();

			when(mock.uploadImages(any(), anyList())).thenReturn(List.of(fakeImage));
			return mock;
		}
	}

	@Test
	@DisplayName("가게 등록 요청 성공")
	void 가게_등록_요청_성공() {

		//setUp
		StoreSaveRequest storeSaveRequest = new StoreSaveRequest(
			"가게이름",
			339923239L,
			"010-1234-5678",
			"가게주소",
			StoreStatus.OPEN,
			StoreType.CAFE
		);

		List<MultipartFile> images = new ArrayList<>();

		MockMultipartFile image = new MockMultipartFile(
			"image",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"fake-image-content".getBytes()
		);

		MockMultipartFile image2 = new MockMultipartFile(
			"image",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"fake-image-content".getBytes()
		);

		images.add(image);
		images.add(image2);

		//로직
		StoreResponse storeResponse = storeService.saveStore(storeSaveRequest, images);
		System.out.println("storeResponse = " + storeResponse);
		ApiResponse<StoreResponse> apiResponse = ApiResponse.of(SuccessCode.STORE_CREATED, storeResponse);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("가게 등록이 완료되었습니다.");
	}

	@Test
	@DisplayName("가게 정보 및 이미지 수정")
	void 가게_정보_및_이미지_수정() {

		//setUp
		StoreUpdateRequest storeSaveRequest = new StoreUpdateRequest(
			"수정된 가게이름",
			"010-1234-5678",
			"가게주소",
			StoreStatus.OPEN,
			StoreType.CAFE
		);

		List<MultipartFile> images = new ArrayList<>();

		MockMultipartFile image = new MockMultipartFile(
			"image",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"fake-image-content".getBytes()
		);

		MockMultipartFile image2 = new MockMultipartFile(
			"image",
			"test.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"fake-image-content".getBytes()
		);

		images.add(image);
		images.add(image2);

		//로직
		StoreResponse storeResponse = storeService.updateStore(1L, storeSaveRequest, images);
		System.out.println("storeResponse = " + storeResponse);
		ApiResponse<StoreResponse> apiResponse = ApiResponse.of(SuccessCode.STORE_UPDATED, storeResponse);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("가게 수정이 완료되었습니다.");
	}


	@Test
	@DisplayName("가게 삭제 테스트")
	void 가게_삭제_테스트() {
		storeService.deleteStore(1L);
		ApiResponse<StoreResponse> apiResponse = ApiResponse.of(SuccessCode.STORE_DELETED, null);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("가게 삭제가 완료되었습니다.");
	}
}

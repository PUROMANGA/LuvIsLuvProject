package com.example.luvisluvproject.domain.store.controller;

import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
import com.example.luvisluvproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
import com.example.luvisluvproject.domain.store.service.StoreService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 가게 관련 요청을 처리하는 REST 컨트롤러
 * 등록, 수정, 삭제, 조회 기능을 제공함
 */
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	/**
	 * 가게 등록 요청
	 * - multipart/form-data 형식으로 JSON 데이터(`request`)와 이미지 파일을 함께 받음
	 * - 이미지가 존재할 경우 S3에 업로드 -> Image 테이블에 저장
	 *
	 * @param request 가게 등록 요청 정보
	 * @param images 업로드할 이미지 파일 리스트 (선택, null 또는 비어있을 수 있음)
	 * @return 등록된 가게 정보
	 */
	@PreAuthorize("hasRole('MANAGER')")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<StoreResponse>> saveStore(
		@RequestPart("request") @Valid StoreSaveRequest request,
		@RequestPart(value = "images", required = false) List<MultipartFile> images
	) {
		StoreResponse response = storeService.saveStore(request, images);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.STORE_CREATED, response));
	}

	/**
	 * 가게 정보 및 이미지 수정 요청 처리
	 * - multipart/form-data 형식으로 request + images 동시 수신
	 * - 주소가 변경되면 위도/경도 갱신
	 * - 이미지가 첨부되면 기존 이미지 삭제 후 새 이미지로 교체
	 *
	 * @param id 수정할 가게의 ID
	 * @param request 수정 요청 정보 (JSON 형식)
	 * @param images 새로 업로드할 이미지 파일 목록 (선택 null 이여도 도;ㅁ)
	 * @return 수정된 가게 정보 (ApiResponse로 감싼 응답)
	 */
	@PreAuthorize("hasRole('MANAGER')")
	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
		@PathVariable Long id,
		@RequestPart("request") @Valid StoreUpdateRequest request,
		@RequestPart(value = "images", required = false) List<MultipartFile> images
	) {
		StoreResponse response = storeService.updateStore(id, request, images);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.STORE_UPDATED, response));
	}

	/**
	 * 가게 삭제
	 *
	 * @param id 가게 ID
	 * @return 삭제 성공 메시지 (body 없음)
	 */
	@PreAuthorize("hasRole('MANAGER')")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable Long id) {
		storeService.deleteStore(id);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.STORE_DELETED));
	}

	/**
	 * 사용자의 현재 좌표로부터 일정 반경 내 가게 목록 조회
	 *
	 * @param lat 사용자의 위도
	 * @param lng 사용자의 경도
	 * @param radiusMeters 반경 거리 (미터 단위, 기본값: 2000m)
	 * @return 반경 내 가게 목록 (성공 메시지 포함)
	 */
	@GetMapping("/nearby")
	public ResponseEntity<ApiResponse<List<StoreResponse>>> getNearbyStores(
		@RequestParam("lat") double lat,
		@RequestParam("lng") double lng,
		@RequestParam(value = "radius", defaultValue = "2000") int radiusMeters
	) {
		List<StoreResponse> stores = storeService.getNearbyStores(lat, lng, radiusMeters);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.STORE_LIST_RETRIEVED, stores));
	}
}

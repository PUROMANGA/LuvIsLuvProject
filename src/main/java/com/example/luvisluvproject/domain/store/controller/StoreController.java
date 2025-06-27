package com.example.luvisluvproject.domain.store.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
import com.example.luvisluvproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
import com.example.luvisluvproject.domain.store.service.StoreService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
	 * 가게 등록 요청 처리
	 *
	 * @param request 가게 등록 요청 DTO
	 * @return 등록된 가게 정보 (성공 메시지 포함)
	 */
	@PreAuthorize("hasRole('MANAGER')")
	@PostMapping
	public ResponseEntity<ApiResponse<StoreResponse>> saveStore(@RequestBody @Valid StoreSaveRequest request) {
		StoreResponse response = storeService.saveStore(request);
		return ResponseEntity.ok(ApiResponse.of(SuccessCode.STORE_CREATED, response));
	}

	/**
	 * 가게 정보 수정
	 *
	 * @param id 가게 ID
	 * @param request 수정 요청 DTO
	 * @return 수정된 가게 정보 (성공 메시지 포함)
	 */
	@PreAuthorize("hasRole('MANAGER')")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
		@PathVariable Long id,
		@RequestBody @Valid StoreUpdateRequest request
	) {
		StoreResponse response = storeService.updateStore(id, request);
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

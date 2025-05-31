package com.example.luvisluvproject.domain.store.controller;

import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
import com.example.luvisluvproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
import com.example.luvisluvproject.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 가게 관련 요청을 처리하는 REST 컨트롤러
 * 등록, 수정, 삭제 기능을 제공함
 */
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;

	/**
	 * 가게 등록 요청 처리
	 * 전달받은 StoreSaveRequest를 통해 가게를 등록하고,
	 * 저장된 정보를 응답으로 반환함
	 *
	 * @param request 가게 등록 요청 DTO
	 * @return ResponseEntity<StoreResponse> 등록된 가게 정보
	 */
	@PostMapping
	public ResponseEntity<StoreResponse> saveStore(@RequestBody @Valid StoreSaveRequest request) {
		StoreResponse response = storeService.saveStore(request);
		return ResponseEntity.ok(response);
	}

	/**
	 * 가게 정보 수정
	 *
	 * @param id 가게 ID
	 * @param request 수정 요청 DTO
	 * @return 수정된 가게 응답 DTO
	 */
	@PutMapping("/{id}")
	public ResponseEntity<StoreResponse> updateStore(@PathVariable Long id,
		@RequestBody @Valid StoreUpdateRequest request) {
		StoreResponse response = storeService.updateStore(id, request);
		return ResponseEntity.ok(response);
	}

	/**
	 * 가게 삭제
	 *
	 * @param id 가게 ID
	 * @return 204 No Content
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
		storeService.deleteStore(id);
		return ResponseEntity.noContent().build();
	}

}

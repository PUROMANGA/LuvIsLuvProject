package com.example.luvisluvproject.domain.store.controller;

import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
import com.example.luvisluvproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
import com.example.luvisluvproject.domain.store.enums.StoreStatus;
import com.example.luvisluvproject.domain.store.enums.StoreType;
import com.example.luvisluvproject.domain.store.service.StoreService;
import com.example.luvisluvproject.global.config.TestConfig;
import com.example.luvisluvproject.global.success.SuccessCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

/**
 * StoreController 통합 테스트
 */

@ActiveProfiles("test")
@WebMvcTest(controllers = StoreController.class)
@Import(TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class StoreControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private StoreService storeService;

	@Test
	@WithMockUser(roles = "MANAGER")
	@DisplayName("가게_등록_성공")
	void saveStoreSuccess() throws Exception {
		// given
		StoreSaveRequest request = new StoreSaveRequest("테스트 가게", 1111222233L, "010-1234-5678", "서울시 구로구",
			StoreStatus.OPEN, StoreType.BAR);

		StoreResponse mockResponse = new StoreResponse(1L, "테스트 가게", 1111222233L, "010-1234-5678", "서울시 구로구",
			StoreStatus.OPEN, StoreType.BAR, null, null);

		when(storeService.saveStore(Mockito.any(StoreSaveRequest.class))).thenReturn(mockResponse);

		// when & then
		mockMvc.perform(
				post("/stores").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SuccessCode.STORE_CREATED.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.STORE_CREATED.getMessage()))
			.andExpect(jsonPath("$.data.name").value("테스트 가게"))
			.andExpect(jsonPath("$.data.status").value("OPEN"))
			.andExpect(jsonPath("$.data.storeType").value("BAR"));
	}

	@WithMockUser(roles = "MANAGER")
	@Test
	@DisplayName("가게_수정_성공")
	void updateStoreSuccess() throws Exception {
		// given
		Long storeId = 1L;

		StoreUpdateRequest updateRequest = new StoreUpdateRequest("수정된 가게", "010-9999-8888", "서울시 마포구",
			StoreStatus.CLOSED, StoreType.RESTAURANT);

		StoreResponse mockResponse = new StoreResponse(storeId, "수정된 가게", 123456789L, "010-9999-8888", "서울시 마포구",
			StoreStatus.CLOSED, StoreType.RESTAURANT, null, null);

		when(storeService.updateStore(eq(storeId), Mockito.any(StoreUpdateRequest.class))).thenReturn(mockResponse);

		// when & then
		mockMvc.perform(put("/stores/{id}", storeId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SuccessCode.STORE_UPDATED.name()))
			.andExpect(jsonPath("$.data.name").value("수정된 가게"))
			.andExpect(jsonPath("$.data.status").value("CLOSED"))
			.andExpect(jsonPath("$.data.storeType").value("RESTAURANT"));
	}

	@WithMockUser(roles = "MANAGER")
	@Test
	@DisplayName("가게_삭제_성공")
	void deleteStoreSuccess() throws Exception {
		// given
		Long storeId = 2L;

		doNothing().when(storeService).deleteStore(storeId);

		// when & then
		mockMvc.perform(delete("/stores/{id}", storeId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SuccessCode.STORE_DELETED.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.STORE_DELETED.getMessage()))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	@DisplayName("내_주변_가게_조회_성공")
	void getNearbyStoresSuccess() throws Exception {
		// given
		double lat = 37.4981;
		double lng = 127.0276;
		int radius = 3000;

		List<StoreResponse> mockStoreList = List.of(
			new StoreResponse(3L, "주변 가게", 123123123L, "010-8888-9999", "서울시 강남구", StoreStatus.OPEN, StoreType.CAFE,
				lat, lng));

		when(storeService.getNearbyStores(lat, lng, radius)).thenReturn(mockStoreList);

		// when & then
		mockMvc.perform(get("/stores/nearby").param("lat", String.valueOf(lat))
				.param("lng", String.valueOf(lng))
				.param("radius", String.valueOf(radius)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(SuccessCode.STORE_LIST_RETRIEVED.name()))
			.andExpect(jsonPath("$.message").value(SuccessCode.STORE_LIST_RETRIEVED.getMessage()))
			.andExpect(jsonPath("$.data", hasSize(1)))
			.andExpect(jsonPath("$.data[0].name").value("주변 가게"));
	}

}

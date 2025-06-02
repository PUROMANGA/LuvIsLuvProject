// package com.example.luvisluvproject.domain.store.controller;
//
// import com.example.luvisluvproject.domain.store.dto.request.StoreSaveRequest;
// import com.example.luvisluvproject.domain.store.dto.request.StoreUpdateRequest;
// import com.example.luvisluvproject.domain.store.dto.response.StoreResponse;
// import com.example.luvisluvproject.domain.store.enums.StoreStatus;
// import com.example.luvisluvproject.domain.store.enums.StoreType;
// import com.example.luvisluvproject.domain.store.service.StoreService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//
//
// import org.springframework.http.MediaType;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
//
// import java.util.List;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// /**
//  * StoreController의 기능을 검증하는 WebMvc 통합 테스트 클래스입니다.
//  */
// @WebMvcTest(StoreController.class)
// @AutoConfigureMockMvc(addFilters = false) // Spring Security 필터 비활성화!
// class StoreControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@MockitoBean
// 	private StoreService storeService;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@Test
// 	@DisplayName("가게 등록 요청이 성공하면 200과 등록된 정보가 반환된다")
// 	void saveStore_success() throws Exception {
// 		StoreSaveRequest request = new StoreSaveRequest(
// 			"무지개 바", 1234567890L, "010-1234-5678",
// 			"서울시 마포구", StoreStatus.OPEN, StoreType.BAR
// 		);
//
// 		StoreResponse response = StoreResponse.builder()
// 			.id(1L)
// 			.name(request.getName())
// 			.businessNumber(request.getBusinessNumber())
// 			.contactNumber(request.getContactNumber())
// 			.address(request.getAddress())
// 			.status(request.getStatus())
// 			.storeType(request.getStoreType())
// 			.latitude(null)
// 			.longitude(null)
// 			.build();
//
// 		Mockito.when(storeService.saveStore(any())).thenReturn(response);
//
// 		mockMvc.perform(post("/stores")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.id").value(1L))
// 			.andExpect(jsonPath("$.name").value("무지개 바"));
// 	}
//
// 	@Test
// 	@DisplayName("가게 수정 요청이 성공하면 200과 수정된 정보가 반환된다")
// 	void updateStore_success() throws Exception {
// 		StoreUpdateRequest request = new StoreUpdateRequest(
// 			"수정된 가게", "010-5678-1234", "서울시 성동구",
// 			StoreStatus.OPEN, StoreType.CAFE
// 		);
//
// 		StoreResponse response = StoreResponse.builder()
// 			.id(1L)
// 			.name(request.getName())
// 			.businessNumber(1234567890L)
// 			.contactNumber(request.getContactNumber())
// 			.address(request.getAddress())
// 			.status(request.getStatus())
// 			.storeType(request.getStoreType())
// 			.latitude(null)
// 			.longitude(null)
// 			.build();
//
// 		Mockito.when(storeService.updateStore(eq(1L), any())).thenReturn(response);
//
// 		mockMvc.perform(put("/stores/1")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.name").value("수정된 가게"));
// 	}
//
// 	@Test
// 	@DisplayName("가게 삭제 요청이 성공하면 204 No Content를 반환한다")
// 	void deleteStore_success() throws Exception {
// 		mockMvc.perform(delete("/stores/1"))
// 			.andExpect(status().isNoContent());
// 	}
//
// 	@Test
// 	@DisplayName("근처 가게 조회 요청이 성공하면 200과 가게 리스트를 반환한다")
// 	void getNearbyStores_success() throws Exception {
// 		List<StoreResponse> stores = List.of(
// 			StoreResponse.builder()
// 				.id(1L)
// 				.name("근처 무지개 바")
// 				.businessNumber(1234567890L)
// 				.contactNumber("010-1234-5678")
// 				.address("서울시 마포구")
// 				.status(StoreStatus.OPEN)
// 				.storeType(StoreType.BAR)
// 				.latitude(37.5665)
// 				.longitude(126.9780)
// 				.build()
// 		);
//
// 		Mockito.when(storeService.getNearbyStores(37.5665, 126.9780, 2000))
// 			.thenReturn(stores);
//
// 		mockMvc.perform(get("/stores/nearby")
// 				.param("lat", "37.5665")
// 				.param("lng", "126.9780")
// 				.param("radius", "2000"))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$[0].name").value("근처 무지개 바"))
// 			.andExpect(jsonPath("$[0].latitude").value(37.5665))
// 			.andExpect(jsonPath("$[0].longitude").value(126.9780));
// 	}
// }

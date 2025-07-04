package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.example.luvisluvproject.domain.auth.dto.response.LoginResponseDto;
import com.example.luvisluvproject.domain.block.dto.BlockResponseDto;
import com.example.luvisluvproject.domain.block.dto.BlockUserDto;
import com.example.luvisluvproject.domain.block.service.BlockService;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class BlockFlowTest {

	@Autowired
	private BlockService blockService;

	@Test
	@DisplayName("블록 등록 테스트")
	void 블록_등록_테스트() {
		String meEmail = "test1234@email.com";
		Long userId = 1L;
		BlockResponseDto blockResponseDto = blockService.blockUser(meEmail, userId);
		System.out.println("blockResponseDto = " + blockResponseDto);
		ApiResponse<BlockResponseDto> apiResponse = ApiResponse.of(SuccessCode.BLOCK_USER_SUCCESS, blockResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("유저 차단 성공");
	}

	@Test
	@DisplayName("블록 해제 테스트")
	void 블록_해제_테스트() {
		String meEmail = "test1234@email.com";
		Long userId = 1L;
		blockService.unblockUser(meEmail, userId);
		ApiResponse<LoginResponseDto> apiResponse = ApiResponse.of(SuccessCode.UNBLOCK_USER_SUCCESS, null);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("유저 차단 해제 성공");
	}

	@Test
	@DisplayName("차단한 사용자 조회 테스트")
	void 차단한_사용자_조회_테스트() {
		//email
		String meEmail = "test1234@email.com";
		//Pageable
		Pageable pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC, "creatTime"));
		Slice<BlockUserDto> blockUserDtoSlice = blockService.getBlockedUsers(meEmail, pageable);
		ApiResponse<Slice<BlockUserDto>> apiResponse = ApiResponse.of(SuccessCode.UNBLOCK_USER_SUCCESS, blockUserDtoSlice);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("차단한 사용자 목록 조회 성공");
	}
}

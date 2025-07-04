package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import com.example.luvisluvproject.domain.match.dto.AcceptMatchDto;
import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;
import com.example.luvisluvproject.domain.match.entity.Match;
import com.example.luvisluvproject.domain.match.entity.MatchStatus;
import com.example.luvisluvproject.domain.match.service.MatchService;
import com.example.luvisluvproject.domain.member.dto.MemberMyProfileResponse;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class MatchFlowTest {

	@Autowired
	private MatchService matchService;

	@Test
	@DisplayName("매칭 시스템 테스트")
	void 매칭_테스트() {
		String meEmail = "test1234@email.com";
		List<ResponseMatchMemberDto> responseMatchMemberDtoList = matchService.getMatchMemberListService(meEmail);
		System.out.println("responseMatchMemberDtoList = " + responseMatchMemberDtoList);
		ApiResponse<List<ResponseMatchMemberDto>> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, responseMatchMemberDtoList);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}

	@Test
	@DisplayName("매칭 신청 테스트")
	void 매칭_신청_테스트() {
		String meEmail = "test1234@email.com";
		MatchResponseDto matchResponseDto = matchService.createMatchService(3L, meEmail);
		System.out.println("matchResponseDto = " + matchResponseDto);
		ApiResponse<MatchResponseDto> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, matchResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}

	@Test
	@DisplayName("매칭 허가 테스트")
	void 매칭_허가_테스트() {
		String meEmail = "user2@example.com";
		AcceptMatchDto acceptMatchDto = new AcceptMatchDto(
			MatchStatus.ACCEPTED
		);
		MatchResponseDto matchResponseDto = matchService.patchMatchService(103L, acceptMatchDto, meEmail);
		System.out.println("matchResponseDto = " + matchResponseDto);
		ApiResponse<MatchResponseDto> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, matchResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}

	@Test
	@DisplayName("매칭 거절 테스트")
	void 매칭_거절_테스트() {
		String meEmail = "user1@example.com";
		AcceptMatchDto acceptMatchDto = new AcceptMatchDto(
			MatchStatus.REJECTED
		);
		MatchResponseDto matchResponseDto = matchService.patchMatchService(103L, acceptMatchDto, meEmail);
		System.out.println("matchResponseDto = " + matchResponseDto);
		ApiResponse<MatchResponseDto> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, matchResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}

	@Test
	@DisplayName("내가 받은 매칭 목록 가져오기 테스트")
	void 매칭_목록_가져오기() {
		String meEmail = "user1@example.com";
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "creatTime"));
		Slice<MatchResponseDto> matchResponseDto = matchService.getMatchService(meEmail, pageable);
		System.out.println("matchResponseDto = " + matchResponseDto);
		ApiResponse<Slice<MatchResponseDto>> apiResponse = ApiResponse.of(SuccessCode.SUCCESS_OK, matchResponseDto);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("요청이 정상적으로 처리되었습니다.");
	}
}

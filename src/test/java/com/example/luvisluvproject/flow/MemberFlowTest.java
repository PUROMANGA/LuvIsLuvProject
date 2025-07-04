package com.example.luvisluvproject.flow;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.luvisluvproject.domain.member.dto.MemberDeleteRequest;
import com.example.luvisluvproject.domain.member.dto.MemberFindResponse;
import com.example.luvisluvproject.domain.member.dto.MemberMyProfileResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateProfile;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.member.service.MemberService;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;
import com.example.luvisluvproject.global.success.ApiResponse;
import com.example.luvisluvproject.global.success.SuccessCode;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class MemberFlowTest {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("사용자 정보 조회 테스트")
	void 사용자_정보_조회() {
		String meEmail = "test1234@email.com";
		MemberMyProfileResponse memberMyProfileResponse = memberService.getMyProfile(meEmail);
		System.out.println("memberMyProfileResponse = " + memberMyProfileResponse);
		ApiResponse<MemberMyProfileResponse> apiResponse = ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, memberMyProfileResponse);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("회원 조회 성공");
	}

	@Test
	@DisplayName("상대 회원 정보 조회 테스트")
	void 상대_회원_정보_조회() {
		String meEmail = "test1234@email.com";
		MemberFindResponse memberFindResponse = memberService.getMemberProfile(meEmail, 2L);
		System.out.println("memberFindResponse = " + memberFindResponse);
		ApiResponse<MemberFindResponse> apiResponse = ApiResponse.of(SuccessCode.FIND_MEMBER_SUCCESS, memberFindResponse);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("회원 조회 성공");
	}

	@Test
	@DisplayName("사용자 프로필 수정 테스트")
	void 사용자_프로필_수정() {
		String meEmail = "test1234@email.com";
		MemberUpdateProfile memberUpdateProfile = new MemberUpdateProfile(
			"수정된 내용",
			"testpw1234",
			"newTestPw1234"
		);
		memberService.updateMemberProfile(meEmail, memberUpdateProfile);
		ApiResponse<Void> apiResponse = ApiResponse.of(SuccessCode.UPDATE_MEMBER_SUCCESS, null);
		System.out.println("apiResponse = " + apiResponse);
		assertThat(apiResponse.getMessage()).isEqualTo("회원정보 수정 성공");
	}

	@Test
	@DisplayName("회원 탈퇴 테스트")
	void 회원_탈퇴_테스트() {
		String meEmail = "test1234@email.com";
		MemberDeleteRequest memberDeleteRequest = new MemberDeleteRequest("newTestPw1234");
		memberService.deleteMember(meEmail, memberDeleteRequest);
		ApiResponse<Void> apiResponse = ApiResponse.of(SuccessCode.DELETE_MEMBER_SUCCESS, null);
		System.out.println("apiResponse = " + apiResponse);
		Member member = memberRepository.findByEmail(meEmail)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));
		assertThat(apiResponse.getMessage()).isEqualTo("회원 탈퇴 완료");
		assertThat(member.isStatus()).isEqualTo(true);
	}
}

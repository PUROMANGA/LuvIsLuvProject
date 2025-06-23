package com.example.luvisluvproject.domain.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.luvisluvproject.domain.member.dto.MemberDeleteRequest;
import com.example.luvisluvproject.domain.member.dto.MemberMyProfileResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateProfile;
import com.example.luvisluvproject.domain.member.dto.MemberPasswordRequest;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private MemberService memberService;

	private Member activeMember;
	private Member deletedMember;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		activeMember = Member.builder()
			.id(1L)
			.name("ActiveUser")
			.email("active@example.com")
			.password("encodedPassword")
			.birthday(LocalDate.of(1990, 1, 1))
			.userRole(UserRole.USER)
			.status(false)
			.content("기존 자기소개")
			.likeCount(0L)
			.build();

		deletedMember = Member.builder()
			.id(2L)
			.name("DeletedUser")
			.email("deleted@example.com")
			.password("encodedPassword")
			.birthday(LocalDate.of(1990, 1, 1))
			.userRole(UserRole.USER)
			.status(true)
			.content("삭제된 자기소개")
			.likeCount(0L)
			.build();
	}

	@Test
	@DisplayName("회원 마이프로필 조회 성공")
	void getMyProfile_Success() {
		when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));

		MemberMyProfileResponse response = memberService.getMyProfile(1L);

		assertEquals(activeMember.getId(), response.getUserId());
		assertEquals(activeMember.getName(), response.getName());
		assertEquals(activeMember.getEmail(), response.getEmail());
		assertEquals(activeMember.getBirthday(), response.getBirthday());
		assertEquals(activeMember.getContent(), response.getContent());
	}

	@Test
	@DisplayName("회원 마이프로필 조회 실패 - 회원 없음")
	void getMyProfile_MemberNotFound() {
		when(memberRepository.findById(1L)).thenReturn(Optional.empty());

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.getMyProfile(1L);
		});
		assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
	}

	@Test
	@DisplayName("회원 마이프로필 조회 실패 - 탈퇴 회원")
	void getMyProfile_MemberDeleted() {
		when(memberRepository.findById(2L)).thenReturn(Optional.of(deletedMember));

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.getMyProfile(2L);
		});
		assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
	}

	@Test
	@DisplayName("비밀번호 변경 성공")
	void updatePasswordMember_Success() {
		MemberPasswordRequest request = new MemberPasswordRequest("oldPassword", "newPassword");

		when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
		when(passwordEncoder.matches("oldPassword", activeMember.getPassword())).thenReturn(true);
		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

		memberService.updatePasswordMember(1L, request);

		assertEquals("encodedNewPassword", activeMember.getPassword());
	}

	@Test
	@DisplayName("비밀번호 변경 실패 - 회원 없음")
	void updatePasswordMember_MemberNotFound() {
		MemberPasswordRequest request = new MemberPasswordRequest("oldPassword", "newPassword");

		when(memberRepository.findById(1L)).thenReturn(Optional.empty());

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.updatePasswordMember(1L, request);
		});
		assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
	}

	@Test
	@DisplayName("비밀번호 변경 실패 - 탈퇴 회원")
	void updatePasswordMember_MemberDeleted() {
		MemberPasswordRequest request = new MemberPasswordRequest("oldPassword", "newPassword");

		when(memberRepository.findById(2L)).thenReturn(Optional.of(deletedMember));

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.updatePasswordMember(2L, request);
		});
		assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
	}

	@Test
	@DisplayName("비밀번호 변경 실패 - 이전 비밀번호 불일치")
	void updatePasswordMember_PasswordMismatch() {
		MemberPasswordRequest request = new MemberPasswordRequest("wrongOldPassword", "newPassword");

		when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
		when(passwordEncoder.matches("wrongOldPassword", activeMember.getPassword())).thenReturn(false);

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.updatePasswordMember(1L, request);
		});
		assertEquals(ExceptionCode.PASSWORD_MISMATCH, exception.getExceptionCode());
	}

	@Test
	@DisplayName("비밀번호 변경 실패 - 새 비밀번호가 이전과 동일")
	void updatePasswordMember_SamePassword() {
		MemberPasswordRequest request = new MemberPasswordRequest("samePassword", "samePassword");

		when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
		when(passwordEncoder.matches("samePassword", activeMember.getPassword())).thenReturn(true);

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.updatePasswordMember(1L, request);
		});
		assertEquals(ExceptionCode.SAME_PASSWORD, exception.getExceptionCode());
	}

	@Test
	@DisplayName("회원 삭제 성공")
	void deleteMember_Success() {
		MemberDeleteRequest request = new MemberDeleteRequest("password");

		when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
		when(passwordEncoder.matches("password", activeMember.getPassword())).thenReturn(true);

		memberService.deleteMember(1L, request);

		assertTrue(activeMember.isStatus());
	}

	@Test
	@DisplayName("회원 삭제 실패 - 회원 없음")
	void deleteMember_MemberNotFound() {
		MemberDeleteRequest request = new MemberDeleteRequest("password");

		when(memberRepository.findById(1L)).thenReturn(Optional.empty());

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.deleteMember(1L, request);
		});
		assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
	}

	@Test
	@DisplayName("회원 삭제 실패 - 탈퇴 회원")
	void deleteMember_MemberDeleted() {
		MemberDeleteRequest request = new MemberDeleteRequest("password");

		when(memberRepository.findById(2L)).thenReturn(Optional.of(deletedMember));

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.deleteMember(2L, request);
		});
		assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
	}

	@Test
	@DisplayName("회원 삭제 실패 - 비밀번호 불일치")
	void deleteMember_PasswordMismatch() {
		MemberDeleteRequest request = new MemberDeleteRequest("wrongPassword");

		when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));
		when(passwordEncoder.matches("wrongPassword", activeMember.getPassword())).thenReturn(false);

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.deleteMember(1L, request);
		});
		assertEquals(ExceptionCode.PASSWORD_MISMATCH, exception.getExceptionCode());
	}

	@Test
	@DisplayName("자기소개 수정 성공")
	void updateContentMember_Success() {
		MemberUpdateProfile request = new MemberUpdateProfile("새 자기소개");

		when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));

		memberService.updateContentMember(1L, request);

		assertEquals("새 자기소개", activeMember.getContent());
	}

	@Test
	@DisplayName("자기소개 수정 실패 - 회원 없음")
	void updateContentMember_MemberNotFound() {
		MemberUpdateProfile request = new MemberUpdateProfile("자기소개");

		when(memberRepository.findById(1L)).thenReturn(Optional.empty());

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.updateContentMember(1L, request);
		});
		assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
	}

	@Test
	@DisplayName("자기소개 수정 실패 - 탈퇴 회원")
	void updateContentMember_MemberDeleted() {
		MemberUpdateProfile request = new MemberUpdateProfile("자기소개");

		when(memberRepository.findById(2L)).thenReturn(Optional.of(deletedMember));

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.updateContentMember(2L, request);
		});
		assertEquals(ExceptionCode.MEMBER_NOT_FOUND, exception.getExceptionCode());
	}

	@Test
	@DisplayName("자기소개 수정 실패 - 동일한 내용")
	void updateContentMember_SameContent() {
		MemberUpdateProfile request = new MemberUpdateProfile("기존 자기소개");

		when(memberRepository.findById(1L)).thenReturn(Optional.of(activeMember));

		CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> {
			memberService.updateContentMember(1L, request);
		});
		assertEquals(ExceptionCode.SAME_CONTENT, exception.getExceptionCode());
	}
}

package com.example.luvisluvproject.domain.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.luvisluvproject.domain.auth.dto.request.LoginRequestDto;
import com.example.luvisluvproject.domain.auth.dto.request.SignupRequestDto;
import com.example.luvisluvproject.domain.auth.dto.request.SignupUserRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.LoginResponseDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupResponseDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupUserResponseDto;
import com.example.luvisluvproject.domain.auth.service.AuthService;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.config.JwtUtil;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("유저 회원가입 성공")
	void signupUserSuccess() {
		SignupUserRequestDto dto = new SignupUserRequestDto("박회원", "park1@email.com", "Test1234!",
			LocalDate.of(2000, 1, 1), "안녕하세요.");
		given(memberRepository.existsByEmail(anyString())).willReturn(false); // 이메일 중복 아님
		given(memberRepository.existsByName(anyString())).willReturn(false); // 이름 중복 아님
		given(passwordEncoder.encode(anyString())).willReturn("encoded_pw"); // 비밀번호 암호화 결과
		given(memberRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0)); // 저장된 멤버 그대로 반환

		SignupUserResponseDto response = authService.signupUser(dto);

		assertEquals("박회원", response.getName());
		assertEquals("park1@email.com", response.getEmail());
		assertEquals(UserRole.USER, response.getUserRole());
	}

	@Test
	@DisplayName("사장 회원가입 성공")
	void signupManagerSuccess() {
		SignupRequestDto dto = new SignupRequestDto("김사장", "kim1@email.com", "Test1234!",
			LocalDate.of(2000, 1, 1), "MANAGER");
		given(memberRepository.existsByEmail(anyString())).willReturn(false); // 이메일 중복 아님
		given(memberRepository.existsByName(anyString())).willReturn(false); // 이름 중복 아님
		given(passwordEncoder.encode(anyString())).willReturn("encoded_pw"); // 비밀번호 암호화 결과
		given(memberRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0)); // 저장된 멤버 그대로 반환

		SignupResponseDto response = authService.signupManager(dto);

		assertEquals("김사장", response.getName());
		assertEquals("kim1@email.com", response.getEmail());
		assertEquals(UserRole.MANAGER, response.getUserRole());
	}

	@Test
	@DisplayName("관리자 회원가입 성공")
	void signupAdminSuccess() {
		SignupRequestDto dto = new SignupRequestDto("정관리", "jung1@email.com", "Test1234!",
			LocalDate.of(2000, 1, 1), "ADMIN");
		given(memberRepository.existsByEmail(anyString())).willReturn(false); // 이메일 중복 아님
		given(memberRepository.existsByName(anyString())).willReturn(false); // 이름 중복 아님
		given(passwordEncoder.encode(anyString())).willReturn("encoded_pw"); // 비밀번호 암호화 결과
		given(memberRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0)); // 저장된 멤버 그대로 반환

		SignupResponseDto response = authService.signupAdmin(dto);

		assertEquals("정관리", response.getName());
		assertEquals("jung1@email.com", response.getEmail());
		assertEquals(UserRole.ADMIN, response.getUserRole());
	}

	@Test
	@DisplayName("이메일 중복이면 예외 발생")
	void emailDuplicate() {
		SignupUserRequestDto dto = new SignupUserRequestDto("박회원", "park1@email.com", "Test1234!",
			LocalDate.of(2000, 1, 1), "안녕하세요.");
		given(memberRepository.existsByEmail(anyString())).willReturn(true); // 이메일 중복

		CustomRuntimeException ex = assertThrows(CustomRuntimeException.class, () -> authService.signupUser(dto));

		assertEquals(ExceptionCode.EMAIL_ALREADY_EXIST, ex.getExceptionCode());
	}

	@Test
	@DisplayName("이름 중복이면 예외 발생")
	void nameDuplicate() {
		SignupUserRequestDto dto = new SignupUserRequestDto("박회원", "park1@email.com", "Test1234!",
			LocalDate.of(2000, 1, 1), "안녕하세요.");
		given(memberRepository.existsByEmail(anyString())).willReturn(false); // 이메일 중복 X
		given(memberRepository.existsByName(anyString())).willReturn(true);  // 이름 중복 O

		CustomRuntimeException ex = assertThrows(CustomRuntimeException.class, () -> authService.signupUser(dto));

		assertEquals(ExceptionCode.NAME_ALREADY_EXIST, ex.getExceptionCode());
	}

	@Test
	@DisplayName("만 19세 미만이면 예외 발생")
	void underage() {
		SignupUserRequestDto dto = new SignupUserRequestDto("박회원", "park1@email.com", "Test1234!",
			LocalDate.of(2010, 1, 1), "안녕하세요."); // 미성년자
		given(memberRepository.existsByEmail(anyString())).willReturn(false); // 이메일 중복 X
		given(memberRepository.existsByName(anyString())).willReturn(false); // 이름 중복 X

		CustomRuntimeException ex = assertThrows(CustomRuntimeException.class, () -> authService.signupUser(dto));

		assertEquals(ExceptionCode.UNDERAGE_USER, ex.getExceptionCode());
	}

	@Test
	@DisplayName("생일이 오늘 날짜보다 미래면 예외 발생")
	void birthdayInFutureThrowsException() {
		LocalDate futureDate = LocalDate.now().plusDays(1); // 내일 날짜
		SignupUserRequestDto dto = new SignupUserRequestDto("박회원", "park1@email.com", "Test1234!",
			futureDate, "안녕하세요.");

		given(memberRepository.existsByEmail(anyString())).willReturn(false);
		given(memberRepository.existsByName(anyString())).willReturn(false);

		CustomRuntimeException ex = assertThrows(CustomRuntimeException.class, () -> authService.signupUser(dto));

		assertEquals(ExceptionCode.INVALID_BIRTHDAY_IN_FUTURE, ex.getExceptionCode());
	}

	@Test
	@DisplayName("존재하지 않는 이메일이면 예외 발생")
	void memberNotFound() {
		LoginRequestDto dto = new LoginRequestDto("park1@email.com", "Test1234!");
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());

		CustomRuntimeException ex = assertThrows(CustomRuntimeException.class, () -> authService.login(dto));

		assertEquals(ExceptionCode.MEMBER_NOT_FOUND, ex.getExceptionCode());
	}

	@Test
	@DisplayName("비밀번호 불일치시 예외 발생")
	void passwordMismatch() {
		Member member = new Member("박회원", "park1@email.com", "Test1234!",
			LocalDate.of(2000, 1, 1), UserRole.USER); // 저장된 회원 정보
		LoginRequestDto dto = new LoginRequestDto("park1@email.com", "Test1234@@@"); // 로그인 시도 정보
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member)); // 이메일로 회원 조회됨
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false); // 비밀번호 일치하지 않음

		CustomRuntimeException ex = assertThrows(CustomRuntimeException.class, () -> authService.login(dto));

		assertEquals(ExceptionCode.LOGIN_FAILED, ex.getExceptionCode());
	}

	@Test
	@DisplayName("정상 로그인 시 토큰 발급")
	void loginSuccess() {
		Member member = new Member("박회원", "park1@email.com", "encoded_pw",
			LocalDate.of(2000, 1, 1), UserRole.USER); // 가입된 회원
		LoginRequestDto dto = new LoginRequestDto("park1@email.com", "encoded_pw"); // 로그인 요청

		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member)); // 이메일로 회원 조회 성공
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true); // 비밀번호 일치
		given(jwtUtil.createAccessToken(any(), any())).willReturn("access-token"); // Access 토큰 생성
		given(jwtUtil.createRefreshToken(any())).willReturn("refresh-token");     // Refresh 토큰 생성

		LoginResponseDto response = authService.login(dto);

		assertEquals("Bearer access-token", response.getAccessToken());
		assertEquals("Bearer refresh-token", response.getRefreshToken());
	}

	@Test
	@DisplayName("유효하지 않은 토큰이면 예외 발생")
	void invalidToken() {
		String token = "invalid-token";
		given(jwtUtil.validateToken(token)).willReturn(false); // 토큰 검증 실패

		CustomRuntimeException ex = assertThrows(CustomRuntimeException.class, () -> authService.logout(token));

		assertEquals(ExceptionCode.INVALID_TOKEN, ex.getExceptionCode());
	}

	@Test
	@DisplayName("유효한 토큰이면 블랙리스트에 저장")
	void logoutSuccess() {
		String token = "valid-token";
		long expiration = 60000L;

		ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(jwtUtil.validateToken(token)).willReturn(true);
		given(jwtUtil.getExpiration(token)).willReturn(expiration);

		assertDoesNotThrow(() -> authService.logout(token));

		then(redisTemplate.opsForValue()).should()
			.set(token, "logout", expiration, TimeUnit.MILLISECONDS);
	}
}
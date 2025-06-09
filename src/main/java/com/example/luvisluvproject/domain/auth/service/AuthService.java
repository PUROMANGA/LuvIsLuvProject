package com.example.luvisluvproject.domain.auth.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.auth.dto.request.LoginRequestDto;
import com.example.luvisluvproject.domain.auth.dto.request.SignupRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.LoginResponseDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupResponseDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.enums.UserRole;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.config.JwtUtil;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;

	/**
	 * 회원가입
	 *
	 * @param requestDto 회원 가입 요청 정보
	 * @return 가입된 회원 정보가 담긴 응답 DTO
	 * @throws CustomRuntimeException 이메일 중복일 경우 {@code ExceptionCode.EMAIL_ALREADY_EXIST}
	 * @throws CustomRuntimeException 이름 중복일 경우 {@code ExceptionCode.NAME_ALREADY_EXIST}
	 * @throws CustomRuntimeException 미성년자 확인 {@code ExceptionCode.UNDERAGE_USER}
	 */
	@Transactional
	public SignupResponseDto signup(SignupRequestDto requestDto) {

		// 이메일 중복확인
		if (memberRepository.existsByEmail(requestDto.getEmail())) {
			throw new CustomRuntimeException(ExceptionCode.EMAIL_ALREADY_EXIST);
		}

		// 이름 중복확인
		if (memberRepository.existsByName(requestDto.getName())) {
			throw new CustomRuntimeException(ExceptionCode.NAME_ALREADY_EXIST);
		}

		// 미성년자 확인
		// TODO 생일을 현재 날짜 이후로 설정한 경우 -> 예외
		LocalDate birthday = requestDto.getBirthday();
		LocalDate today = LocalDate.now();

		if (birthday.plusYears(19).isAfter(today)) {
			throw new CustomRuntimeException(ExceptionCode.UNDERAGE_USER);
		}

		// 비밀번호 암호화
		String encodePassword = passwordEncoder.encode(requestDto.getPassword());

		// userRole string -> enum 변환
		UserRole userRole = UserRole.of(requestDto.getUserRole());

		// TODO userrole api 분리
		Member member = new Member(
			requestDto.getName(),
			requestDto.getEmail(),
			encodePassword,
			requestDto.getBirthday(),
			userRole
		);

		Member saved = memberRepository.save(member);

		return new SignupResponseDto(
			saved.getId(),
			saved.getName(),
			saved.getEmail(),
			saved.getBirthday(),
			saved.getUserRole()
		);
	}

	/**
	 * 로그인 및 JWT 토큰 발급
	 *
	 * @param requestDto 로그인 요청 정보
	 * @return 발급된 액세스 토큰과 리프레시 토큰이 담긴 응답 DTO
	 * @throws CustomRuntimeException 회원이 존재하지 않거나 로그인 실패 시
	 */
	@Transactional
	public LoginResponseDto login(LoginRequestDto requestDto) {

		// 이메일 체크해 실존하는 멤버인지 체크
		Member member = memberRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		// 비밀번호 일치 확인
		if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
			throw new CustomRuntimeException(ExceptionCode.LOGIN_FAILED);
		}

		// 맞으면 토큰 발급
		String accessToken = jwtUtil.createAccessToken(member.getEmail(), member.getUserRole().name());
		String refreshToken = jwtUtil.createRefreshToken(member.getEmail());

		return new LoginResponseDto("Bearer " + accessToken, "Bearer " + refreshToken);

	}

	/**
	 * 로그아웃
	 *
	 * @param accessToken 클라이언트가 사용 중인 JWT Access Token
	 * @throws CustomRuntimeException 유효하지 않은 토큰일 경우
	 */
	@Transactional
	public void logout(String accessToken) {
		if (!jwtUtil.validateToken(accessToken)) {
			throw new CustomRuntimeException(ExceptionCode.INVALID_TOKEN);
		}

		// 토큰 남은 유효 시간 계산
		long expiration = jwtUtil.getExpiration(accessToken);

		// Redis에 access token을 블랙리스트로 저장
		redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
	}

}

package com.example.luvisluvproject.domain.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.luvisluvproject.domain.auth.common.AuthServiceHelper;
import com.example.luvisluvproject.domain.auth.dto.request.LoginRequestDto;
import com.example.luvisluvproject.domain.auth.dto.request.SignupRequestDto;
import com.example.luvisluvproject.domain.auth.dto.request.SignupUserRequestDto;
import com.example.luvisluvproject.domain.auth.dto.response.LoginResponseDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupResponseDto;
import com.example.luvisluvproject.domain.auth.dto.response.SignupUserResponseDto;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.global.config.JwtUtil;
import com.example.luvisluvproject.global.error.CustomRuntimeException;
import com.example.luvisluvproject.global.error.ExceptionCode;

import lombok.RequiredArgsConstructor;

/**
 * 인증 관련 로직을 처리하는 서비스 클래스입니다.
 * 회원가입, 로그인, 로그아웃, 토큰 재발급 기능을 제공합니다.
 */
@Service
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RedisTemplate<String, String> redisTemplate;
	private final AuthServiceHelper authServiceHelper;

	public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
		@Qualifier("tokenRedisTemplate") RedisTemplate<String, String> redisTemplate, AuthServiceHelper authServiceHelper) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.redisTemplate = redisTemplate;
		this.authServiceHelper = authServiceHelper;

	/**
	 * 사용자의 회원가입을 처리합니다.
	 *
	 * @param requestDto 회원가입 요청 데이터
	 * @return 회원가입 결과 응답 DTO
	 */
	@Transactional
	public SignupResponseDto signup(SignupUserRequestDto requestDto) {
		String encodePassword = passwordEncoder.encode(requestDto.getPassword());
		Member member = authServiceHelper.createMember(
			requestDto.getEmail(), requestDto.getName(),
			requestDto.getBirthday(), encodePassword, requestDto.getUserRole());

		return new SignupResponseDto(
			member.getId(),
			member.getName(),
			member.getEmail(),
			member.getBirthday(),
			member.getUserRole()
		);
	}

	/**
	 * 사용자의 로그인을 처리하고 AccessToken과 RefreshToken을 발급합니다.
	 *
	 * @param requestDto 로그인 요청 데이터
	 * @return 로그인 결과 응답 DTO (액세스 토큰, 리프레시 토큰)
	 */
	@Transactional
	public LoginResponseDto login(LoginRequestDto requestDto) {
		Member member = memberRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
			throw new CustomRuntimeException(ExceptionCode.LOGIN_FAILED);
		}

		String accessToken = jwtUtil.createAccessToken(member.getEmail(), member.getUserRole().name());
		String refreshToken = jwtUtil.createRefreshToken(member.getEmail());
		long expiration = jwtUtil.getExpiration(refreshToken);

		redisTemplate.opsForValue().set(member.getEmail(), refreshToken, expiration, TimeUnit.MILLISECONDS);
		return new LoginResponseDto("Bearer " + accessToken, "Bearer " + refreshToken);
	}

	/**
	 * 사용자의 로그아웃을 처리하고, AccessToken을 블랙리스트에 등록합니다.
	 *
	 * @param accessToken 로그아웃할 액세스 토큰
	 */
	@Transactional
	public void logout(String accessToken) {
		if (!jwtUtil.validateToken(accessToken)) {
			throw new CustomRuntimeException(ExceptionCode.INVALID_TOKEN);
		}

		long expiration = jwtUtil.getExpiration(accessToken);
		redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
	}

	/**
	 * 유효한 RefreshToken으로 새로운 AccessToken을 발급합니다.
	 *
	 * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰
	 * @return 새로운 액세스 토큰
	 */
	@Transactional
	public String refreshService(String refreshToken) {
		jwtUtil.validateToken(refreshToken);
		String email = jwtUtil.getEmail(refreshToken);

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomRuntimeException(ExceptionCode.MEMBER_NOT_FOUND));

		String savedRefreshToken = redisTemplate.opsForValue().get(email);
		if (!refreshToken.equals(savedRefreshToken)) {
			throw new RuntimeException("부정된 접근입니다.");
		}

		return jwtUtil.createAccessToken(member.getEmail(), member.getUserRole().name());
	}
}

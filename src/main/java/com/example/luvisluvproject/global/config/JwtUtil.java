package com.example.luvisluvproject.global.config;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

	private static final String BEARER_PREFIX = "Bearer ";

	@Value("${jwt.accessToken.time}")
	private long accessTokenExpiration;
	@Value("${jwt.refreshToken.time}")
	private long refreshTokenExpiration;
	@Value("${jwt.secretKey}")
	private String secretKey;

	private static Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] keyBytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(keyBytes);
	}

	// AccessToken 생성
	public String createAccessToken(String email, String userRole) {
		return Jwts.builder()
			.setSubject(email) // 유저 식별
			.claim("userRole", userRole) // 유저 권한
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	// RefreshToken 생성
	public String createRefreshToken(String email) {
		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	// 토큰 유효한지 확인하는 메서드
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key) // 비밀키로 파싱할 준비
				.build()
				.parseClaimsJws(token); // 여기서 서명, 만료 등 다 검증됨

			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.warn("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.warn("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.warn("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.warn("JWT 토큰이 비어있습니다.");
		}
		return false;
	}

	// 토큰에서 정보(Claims)를 추출하는 메서드
	public Claims extractClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody(); // => 이게 Claims임
	}

	// 토큰 추출(요청에서 JWT만 순수하게 꺼내줌)
	public String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader("Authorization");
		if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)) {
			return bearer.substring(BEARER_PREFIX.length());
		}
		return null;
	}

	// 토큰 남은 만료시간 구하기 -> 로그아웃 구현시 사용
	public long getExpiration(String token) {
		Claims claims = extractClaims(token);
		return claims.getExpiration().getTime() - System.currentTimeMillis();
	}

	public String getEmail(String token) {
		token = token.substring(7);
		Claims claims = extractClaims(token);
		return claims.getSubject();
	}
}

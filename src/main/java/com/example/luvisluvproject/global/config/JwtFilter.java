package com.example.luvisluvproject.global.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.luvisluvproject.global.common.AuthUser;
import com.example.luvisluvproject.global.common.UserDetailsServiceImpl;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		List<String> whitelist = List.of(
			"/auth/login",
			"/auth/signup",
			"/ws/**"
		);

		String requestUri = request.getRequestURI();

		if (whitelist.contains(requestUri)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = jwtUtil.resolveToken(request);

		// Redis에 로그아웃(블랙리스트) 처리된 토큰인지 확인
		String isLogout = redisTemplate.opsForValue().get(token);
		if ("logout".equals(isLogout)) {
			log.info("로그아웃된 토큰으로 접근 시도");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그아웃된 토큰입니다.");
			return;
		}

		if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {

			try {
				Claims claims = jwtUtil.extractClaims(token);
				String email = claims.getSubject();
				String role = claims.get("userRole", String.class);

				AuthUser authUser = (AuthUser)userDetailsService.loadUserByUsername(email);

				UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(authUser, null,
						Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			} catch (Exception e) {
				log.warn("JWT 처리 중 오류 발생: {}", e.getMessage());
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰 처리 실패");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

}

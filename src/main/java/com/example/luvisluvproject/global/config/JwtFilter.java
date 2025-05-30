package com.example.luvisluvproject.global.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		List<String> whitelist = List.of(
			"/auth/login",
			"/auth/signup"
		);

		String requestUri = request.getRequestURI();

		if (whitelist.contains(requestUri)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = jwtUtil.resolveToken(request);

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

package com.example.luvisluvproject.domain.chat.common;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		if (request instanceof ServletServerHttpRequest servletServerHttpRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest)servletServerHttpRequest;

			String authHeader = httpServletRequest.getHeader("Authorization");

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);

				//if절로 JWTPROVIDER의 VALIDATETOKEN을 이용해서 TOKEN이 정상적인지 확인
				// 이후 위에 ATTRIBUTE을 사용해서 WEBSOCKERT세션에 저장

			} else {
				throw new RuntimeException("헤더 정보가 없습니다");
			}
		}
		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {

		if (request instanceof ServletServerHttpRequest servletServerHttpRequest) {
			String ip = servletServerHttpRequest.getServletRequest().getRemoteAddr();
			String userAgent = servletServerHttpRequest.getServletRequest().getHeader("User-Agent");

			log.info("WebSocket 연결됨 - IP: {}, UA: {}", ip, userAgent);
		}
	}
}

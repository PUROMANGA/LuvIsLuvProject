package com.example.luvisluvproject.domain.chat.common;

import java.security.Principal;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.example.luvisluvproject.global.config.JwtUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final StompHandler stompHandler;
	private final JwtUtil jwtUtil;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
			.setHandshakeHandler(handshakeHandler())
			.addInterceptors(new HttpHandshakeInterceptor(jwtUtil))
			.setAllowedOriginPatterns("*");
			// .withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/sub");
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
		channelRegistration.interceptors(stompHandler);
	}

	@Bean
	public DefaultHandshakeHandler handshakeHandler() {
		return new DefaultHandshakeHandler() {
			@Override
			protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
				Map<String, Object> attributes) {
				return (Principal) attributes.get("principal");
			}
		};
	}
}
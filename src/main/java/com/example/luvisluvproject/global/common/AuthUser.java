package com.example.luvisluvproject.global.common;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.luvisluvproject.domain.member.entity.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthUser implements UserDetails {

	private final Member member;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// "ROLE_" 접두어는 스프링 시큐리티에서 권한 명칭을 구분하는 컨벤션
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + member.getUserRole().name()));
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getEmail(); // 로그인 ID로 이메일을 쓴다면
	}
}

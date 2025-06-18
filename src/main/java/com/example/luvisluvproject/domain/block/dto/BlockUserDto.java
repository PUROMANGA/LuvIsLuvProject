package com.example.luvisluvproject.domain.block.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BlockUserDto
 * 사용자가 차단한 사용자 정보를 응답으로 제공할 때 사용하는 DTO
 */
@Getter
@AllArgsConstructor
public class BlockUserDto {

	/**
	 * 차단된 사용자의 ID
	 */
	private Long id;

	/**
	 * 차단된 사용자의 이름
	 */
	private String name;

	/**
	 * 차단된 사용자의 이메일
	 */
	private String email;
}

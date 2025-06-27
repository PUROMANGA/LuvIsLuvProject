package com.example.luvisluvproject.domain.block.dto;

import com.example.luvisluvproject.domain.block.entity.Block;

import lombok.Getter;

/**
 * BlockUserDto
 * 사용자가 차단한 사용자 정보를 응답으로 제공할 때 사용하는 DTO
 */
@Getter
public class BlockUserDto {

	/**
	 * 차단된 사용자의 ID
	 */
	private final Long blockedId;

	/**
	 * 차단된 사용자의 이름
	 */
	private final String blockedUserName;

	/**
	 * 차단된 사용자의 이메일
	 */
	private final String blockedUserEmail;

	public BlockUserDto(Block block) {
		this.blockedId = block.getBlocked().getId();
		this.blockedUserName = block.getBlocked().getName();
		this.blockedUserEmail = block.getBlocked().getEmail();
	}
}

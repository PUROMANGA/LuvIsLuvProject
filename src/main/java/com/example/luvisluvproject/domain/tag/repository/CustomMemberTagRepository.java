package com.example.luvisluvproject.domain.tag.repository;

import java.util.List;

import com.example.luvisluvproject.domain.match.dto.MatchMemberDto;

public interface CustomMemberTagRepository {

	List<MatchMemberDto> findMatchMemberDtoFindByEmail(String email);
}

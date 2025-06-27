package com.example.luvisluvproject.domain.tag.repository;

import java.util.List;

import com.example.luvisluvproject.domain.match.dto.ResponseMatchMemberDto;

public interface CustomMemberTagRepository {

	List<ResponseMatchMemberDto> findResponseMatchMemberDtoFindByEmail(String email);
}

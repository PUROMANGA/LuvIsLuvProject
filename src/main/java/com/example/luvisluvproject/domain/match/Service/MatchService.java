package com.example.luvisluvproject.domain.match.Service;

import org.springframework.stereotype.Service;

import com.example.luvisluvproject.domain.match.dto.MatchResponseDto;
import com.example.luvisluvproject.domain.match.repository.MatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MatchService {

	private final MatchRepository matchRepository;

	public MatchResponseDto createMatchService(String email) {

	}
}

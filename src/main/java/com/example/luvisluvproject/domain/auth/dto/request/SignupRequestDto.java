package com.example.luvisluvproject.domain.auth.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignupRequestDto {

	@NotBlank(message = "이름을 입력해주세요.")
	private final String name;

	@Email
	@NotBlank(message = "이메일을 입력해주세요.")
	private final String email;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	private final String password;

	@NotNull(message = "생일을 입력해주세요.")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private final LocalDate birthday;

	@NotBlank(message = "멤버 권한을 입력해주세요.")
	private final String userRole;

}

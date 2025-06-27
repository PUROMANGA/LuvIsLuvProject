package com.example.luvisluvproject.domain.member;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.luvisluvproject.domain.block.repository.BlockRepository;
import com.example.luvisluvproject.domain.member.dto.MemberFindResponse;
import com.example.luvisluvproject.domain.member.dto.MemberMyProfileResponse;
import com.example.luvisluvproject.domain.member.dto.MemberUpdateProfile;
import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.member.service.MemberService;
import com.example.luvisluvproject.global.common.TestFactory;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Spy
	private TestFactory testFactory = new TestFactory(passwordEncoder);

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private BlockRepository blockRepository;

	@InjectMocks
	private MemberService memberService;

	private Member member;
	private Member opponent;

	@Nested
	@DisplayName("멤버 서비스 테스트")
	class MemberTest {

		@Test
		@DisplayName("멤버를 찾아서 제대로 Response 담아주는지")
		void getMyProfileTest() {
			//given
			member = testFactory.loginTestOf("name", "park1@email.com", "Test1234!");
			testFactory.forTestUpdateId(member, 1L);
			given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

			//when
			MemberMyProfileResponse memberMyProfileResponse = memberService.getMyProfile(member.getEmail());

			//then
			assertThat(memberMyProfileResponse.getUserId()).isEqualTo(1L);
			assertThat(memberMyProfileResponse.getName()).isEqualTo("name");
			assertThat(memberMyProfileResponse.getEmail()).isEqualTo("park1@email.com");
			assertThat(memberMyProfileResponse.getBirthday()).isEqualTo(LocalDate.of(2000, 1, 1));
			assertThat(memberMyProfileResponse.getAge()).isEqualTo(25);
			assertThat(memberMyProfileResponse.getContent()).isEqualTo("내용");
		}

		@Test
		@DisplayName("상대를 찾아서 제대로 Response 담아주는지")
		void getMemberProfileTest() {
			//given
			member = testFactory.loginTestOf("name", "park1@email.com", "Test1234!");
			opponent = testFactory.loginTestOf( "name", "kim1@email.com", "Test1234!");
			testFactory.forTestUpdateId(member, 1L);
			testFactory.forTestUpdateId(opponent, 2L);

			given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
			given(memberRepository.findById(anyLong())).willReturn(Optional.of(opponent));
			given(blockRepository.existsByBlockerAndBlocked(member, opponent)).willReturn(false);

			//when
			MemberFindResponse memberFindResponse = memberService.getMemberProfile(member.getEmail(), opponent.getId());

			//then
			assertThat(memberFindResponse.getUserId()).isEqualTo(2L);
			assertThat(memberFindResponse.getName()).isEqualTo("name");
			assertThat(memberFindResponse.getAge()).isEqualTo(25);
			assertThat(memberFindResponse.getContent()).isEqualTo("내용");
		}

		@Test
		@DisplayName("업데이트한 값이 정상작동으로 들어가는지")
		void updateMemberProfileTest() {
			//given
			member = testFactory.loginTestOf("name", "park1@email.com", "Test1234!");
			testFactory.forTestUpdateId(member, 1L);

			given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

			MemberUpdateProfile memberUpdateProfile = new MemberUpdateProfile(
				"수정된 내용",
				"Test1234!",
				"newtest1234"
			);

			memberService.updateMemberProfile(member.getEmail(), memberUpdateProfile);
			String newPassword = passwordEncoder.encode(memberUpdateProfile.getNewPassword());
			member.updateProfile(newPassword, memberUpdateProfile.getContent());
			assertThat(member.getContent()).isNotEqualTo("내용");
		}
	}
}

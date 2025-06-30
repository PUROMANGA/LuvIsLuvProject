package com.example.luvisluvproject.domain.notify;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.luvisluvproject.domain.member.entity.Member;
import com.example.luvisluvproject.domain.member.repository.MemberRepository;
import com.example.luvisluvproject.domain.notify.dto.NotifyDto;
import com.example.luvisluvproject.domain.notify.entity.Notify;
import com.example.luvisluvproject.domain.notify.repository.NotifyRepository;
import com.example.luvisluvproject.domain.notify.service.NotifyService;
import com.example.luvisluvproject.global.common.TestFactory;

@ExtendWith(MockitoExtension.class)
public class NotifyTest {

	@Spy
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Spy
	private TestFactory testFactory = new TestFactory(passwordEncoder);

	@Mock
	private NotifyRepository notifyRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private NotifyService notifyService;

	@Test
	@DisplayName("알람의 Querry문 확인")
	void getNotifyServiceTest() {

		//setUp
		Member member = testFactory.loginTestOf("name", "park1@email.com", "Test1234!");
		Member opponent = testFactory.loginTestOf( "name", "kim1@email.com", "Test1234!");
		testFactory.forTestUpdateId(member, 1L);
		testFactory.forTestUpdateId(opponent, 2L);

		List<Notify> notifyDtoList = new ArrayList<>();

		NotifyDto notifyDto = new NotifyDto(member, opponent, NotifyCategory.Match, false);
		Notify notify = testFactory.notifyTestOf(notifyDto, opponent, member);

		notifyDtoList.add(notify);

		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "creatTime"));

		Slice<Notify> notifies = new SliceImpl<>(notifyDtoList, pageable, false);

		//given
		given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
		given(notifyRepository.findByMemberId(anyLong(), eq(pageable))).willReturn(notifies);

		//when
		Slice<NotifyDto> notifyDtos = notifyService.getNotifyService(member.getEmail(), pageable);
	}
}

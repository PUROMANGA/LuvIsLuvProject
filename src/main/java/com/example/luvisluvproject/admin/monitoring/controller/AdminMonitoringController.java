package com.example.luvisluvproject.admin.monitoring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.luvisluvproject.domain.memberInteractionLog.dto.MemberInteractionLogDto;
import com.example.luvisluvproject.admin.monitoring.service.AdminMonitoringService;

@RestController
@RequestMapping("/admin")
public class AdminMonitoringController {
	private final AdminMonitoringService adminMonitoringService;

	public AdminMonitoringController(AdminMonitoringService adminMonitoringService) {
		this.adminMonitoringService = adminMonitoringService;
	}

	// //설계중, 어디를 모니터링할까...
	// @GetMapping("/MemberInteractionLogs")
	// public MemberInteractionLogDto getMemberInteractionLog(@RequestParam Long memberId) {
	// 	return adminMonitoringService.getMemberInteractionLogService(memberId);
	// }
}

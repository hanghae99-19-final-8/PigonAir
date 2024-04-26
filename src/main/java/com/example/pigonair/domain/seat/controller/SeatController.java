package com.example.pigonair.domain.seat.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.pigonair.domain.seat.dto.SeatResponseDto;
import com.example.pigonair.domain.seat.service.SeatService;
import com.example.pigonair.global.config.jmeter.JmeterService;
import com.example.pigonair.global.config.security.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/flight")
@RequiredArgsConstructor
public class SeatController {

	private final SeatService seatService;
	private final JmeterService jmeterService;

	@GetMapping("/{flightId}")
	public String getSeatingChart(@PathVariable Long flightId, Model model,
		@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
		List<SeatResponseDto> seatsDto = seatService.getSeatingChart(flightId);
		jmeterService.setTransactionNameBasedOnJMeterTag(request);
		model.addAttribute("seats", seatsDto);
		return "seats/seatList";
	}

}

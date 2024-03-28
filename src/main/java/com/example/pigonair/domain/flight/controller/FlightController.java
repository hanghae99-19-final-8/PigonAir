package com.example.pigonair.domain.flight.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.pigonair.domain.flight.dto.FlightResponseDto;
import com.example.pigonair.domain.flight.service.FlightService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FlightController {
	private final FlightService flightService;

	@GetMapping("/flight/{start_date}/{end_date}/{departure}/{destination}")
	public String getFlightsByConditions(
		@PathVariable("start_date") LocalDateTime startDate,
		@PathVariable("end_date") LocalDateTime endDate,
		@PathVariable("departure") String departure,
		@PathVariable("destination") String destination,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "20") int size,
		@RequestParam(defaultValue = "ASC") String orderBy, Model model) {

		List<FlightResponseDto> flightList = flightService.getFlightsByConditions(startDate, endDate, departure, destination, page, size, orderBy);

		model.addAttribute("flights", flightList);
		return "flight-result";
	}
}
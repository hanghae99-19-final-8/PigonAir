package com.example.pigonair.domain.flight.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.pigonair.domain.flight.dto.FlightResponseDto;
import com.example.pigonair.domain.flight.entity.Flight;
import com.example.pigonair.domain.flight.repository.FlightRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlightService {
	private final FlightRepository flightRepository;

	public List<FlightResponseDto> getFlightsByConditions(LocalDateTime startDate, LocalDateTime endDate,
		String departureCode, String destinationCode, int page, int size, String orderBy) {

		Sort sort = Sort.by(orderBy);

		Pageable pageable = PageRequest.of(page - 1, size, sort);

		Page<Flight> flightsPage = flightRepository.findByDepartureTimeBetweenAndOriginAndDestination(startDate, endDate, departureCode, destinationCode, pageable);

		return flightsPage.getContent().stream()
			.map(FlightResponseDto::new)
			.toList();
	}
}
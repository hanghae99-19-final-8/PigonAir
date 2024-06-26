package com.example.pigonair.domain.reservation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.pigonair.domain.reservation.dto.ReservationRequestDto;
import com.example.pigonair.domain.reservation.dto.ReservationResponseDto;
import com.example.pigonair.domain.reservation.service.ReservationService;
import com.example.pigonair.global.config.common.exception.CustomException;
import com.example.pigonair.global.config.jmeter.JmeterService;
import com.example.pigonair.global.config.security.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j(topic = "수행시간 측정")
public class ReservationController {

	private final ReservationService reservationService;
	private final JmeterService jmeterService;


	@PostMapping("/reservation") // 예약 진행
	public ResponseEntity<?> saveReservation(@RequestBody ReservationRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
		try {
			reservationService.saveReservation(requestDto, userDetails);
			jmeterService.setTransactionNameBasedOnJMeterTag(request);
			return ResponseEntity.ok().build();
		} catch (CustomException e) {
			return ResponseEntity.status(e.getHttpStatus()).build();
		}
	}

	@GetMapping("/reservation")    // 예약 확인
	public String getReservations(@AuthenticationPrincipal UserDetailsImpl userDetails,
		Model model, HttpServletRequest request) {
		try {
			List<ReservationResponseDto> reservations = reservationService.getReservations(userDetails);
			model.addAttribute("reservations", reservations);
			jmeterService.setTransactionNameBasedOnJMeterTag(request);
			return "reservation/reservation_history";
		} catch (CustomException e) {
			log.error("Error occurred during getReservations: {}", e.getMessage());
			return "error"; // 에러 페이지로 리다이렉트 또는 메시지 표시
		}
	}

	@DeleteMapping("/reservation/{reservation_id}") // 예약 취소
	public ResponseEntity<?> cancelReservation(@PathVariable Long reservation_id,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		try {
			reservationService.cancelReservation(reservation_id, userDetails);
			return ResponseEntity.ok().build();
		} catch (CustomException e) {
			log.error("Error occurred during cancelReservation: {}", e.getMessage());
			return ResponseEntity.status(e.getHttpStatus()).build();
		}
	}
}

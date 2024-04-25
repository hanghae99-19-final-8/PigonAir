package com.example.pigonair.domain.seat.controller;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.pigonair.domain.seat.dto.AllowedUserResponse;
import com.example.pigonair.domain.seat.dto.SeatResponseDto;
import com.example.pigonair.domain.seat.service.SeatService;
import com.example.pigonair.global.config.security.UserDetailsImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/seat")
@RequiredArgsConstructor
public class SeatController {

	private final SeatService seatService;
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

	RestTemplate restTemplate = new RestTemplate();

	@GetMapping("/{flightId}")
	public String getSeatingChart(
		@RequestParam(name = "queue", defaultValue = "default") String queue,
		@PathVariable Long flightId, Model model,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		HttpServletRequest request) {

		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();


		if (threadMXBean.getThreadCount() > 1) {
			ResponseEntity<AllowedUserResponse> response = waitSystem(request, queue, userDetails.getUser().getId());
			// 허용 불가 상태
			if (response.getBody() == null || !response.getBody().allowed()) {
				// 대기 웹페이지로 리다이렉트
				return "redirect:http://127.0.0.1:9010/waiting-room?user_id=%d&redirect_url=%s".formatted(
					userDetails.getUser().getId(), "http://127.0.0.1:8080/seat/%d".formatted(flightId));
			}
		}
		List<SeatResponseDto> seatsDto = seatService.getSeatingChart(flightId);
		model.addAttribute("seats", seatsDto);

		return "seats/seatList";
	}

	public ResponseEntity<AllowedUserResponse> waitSystem(HttpServletRequest request, String queue, Long userId) {

		Cookie[] cookies = request.getCookies();
		String cookieName = "user-queue-%s-token".formatted(queue);

		Optional<Cookie> cookie1 = Arrays.stream(cookies)
			.filter(i -> i.getName().equalsIgnoreCase("Authorization"))
			.findFirst();
		String token1 = cookie1.orElse(new Cookie("Authorization", "")).getValue();

		String token = "";
		if (cookies != null) {
			Optional<Cookie> cookie = Arrays.stream(cookies)
				.filter(i -> i.getName().equalsIgnoreCase(cookieName))
				.findFirst();
			token = cookie.orElse(new Cookie(cookieName, "")).getValue();
		}

		URI uri = UriComponentsBuilder
			.fromUriString("http://127.0.0.1:9010")
			.path("/api/v1/queue/allowed")
			.queryParam("queue", queue)
			.queryParam("user_id", userId)
			.queryParam("token", token)
			.encode()
			.build()
			.toUri();

		ResponseEntity<AllowedUserResponse> response = restTemplate.getForEntity(uri, AllowedUserResponse.class);

		return response;
	}

}

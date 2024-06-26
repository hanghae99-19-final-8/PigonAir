package com.example.pigonair.domain.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.pigonair.domain.member.service.MemberServiceImpl;
import com.example.pigonair.domain.payment.dto.PaymentRequestDto.PostPayRequestDto;
import com.example.pigonair.domain.payment.dto.PaymentResponseDto.PayResponseDto;
import com.example.pigonair.domain.payment.service.PaymentServiceImpl;
import com.example.pigonair.global.config.jmeter.JmeterService;
import com.example.pigonair.global.config.security.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentServiceImpl paymentService;
	private final MemberServiceImpl memberService;
	private final JmeterService jmeterService;

	@GetMapping("/pay/{reservationId}")
	public String pay(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model,
		@PathVariable("reservationId") Long id, HttpServletRequest request) {
		PayResponseDto responseDto = paymentService.payProcess(id, userDetails.getUser());
		jmeterService.setTransactionNameBasedOnJMeterTag(request);
		model.addAttribute("responseDto", responseDto);
		return "pay";
	}

	@PostMapping("/pay")
	public ResponseEntity<?> postPay(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody PostPayRequestDto requestDto, Model model, HttpServletRequest request) {
		try {
			paymentService.postPayProcess(requestDto);
			jmeterService.setTransactionNameBasedOnJMeterTag(request);

			return ResponseEntity.ok("결제 완료");
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

}

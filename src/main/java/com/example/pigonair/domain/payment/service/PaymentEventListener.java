package com.example.pigonair.domain.payment.service;

import static com.example.pigonair.domain.payment.dto.PaymentRequestDto.*;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.pigonair.domain.email.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

	private final PaymentService paymentService;
	private final EmailService emailService;

	@RabbitListener(queues = "payment.queue")
	public void handlePaymentCompletedEvent(PostPayRequestDto requestDto) {
		try {
			System.out.printf("Payment completed for payment ID: {}", requestDto.paidAmount() + "\n");
			paymentService.savePayInfo(requestDto);

			String recipientEmail = requestDto.email();
			String subject = "Payment Completed";
			String body = "Payment completed for payment ID: " + requestDto.paidAmount();
			emailService.sendEmail(recipientEmail, subject, body);
		} catch (Exception ex) {
			log.error("Payment 처리 중 오류 발생: {}", ex.getMessage(), ex);
		}

	}
}
